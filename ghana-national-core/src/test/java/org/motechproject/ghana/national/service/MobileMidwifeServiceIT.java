package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollmentTest.assertEnrollment;
import static org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;

public class MobileMidwifeServiceIT extends BaseIntegrationTest {

    @Autowired
    private AllMobileMidwifeEnrollments allEnrollments;
    @Autowired
    private MobileMidwifeService service;
    @Autowired
    private AllCampaigns allCampaigns;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private AllMessageCampaigns allMessageCampaigns;
    @Autowired
    private IdentifierGenerator identifierGenerator;
    @Autowired
    private MessageCampaignService messageCampaignService;

    @Test
    public void shouldCreateMobileMidwifeEnrollmentAndCreateScheduleIfNotRegisteredAlready() {

        Medium medium = Medium.SMS;
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId(identifierGenerator.newPatientId()).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).serviceType(ServiceType.PREGNANCY_TEXT).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(medium).reasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH).
                messageStartWeek("20").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        service.register(enrollment);
        verifyCreateNewEnrollment(enrollment, allCampaigns.nextCycleDateFromToday(enrollment.getServiceType(), medium));
    }

    @Test
    public void shouldRolloverMobileMidwifeEnrollmentAndCreateSchedule() {

        String patientId = identifierGenerator.newPatientId();
        Medium medium = Medium.SMS;
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).serviceType(ServiceType.PREGNANCY_TEXT).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(medium).reasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH).
                messageStartWeek("20").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        service.register(enrollment);
        MobileMidwifeEnrollment newEnrollment = MobileMidwifeEnrollment.cloneNew(enrollment).setMessageStartWeek("41").setServiceType(ServiceType.CHILD_CARE_TEXT);
        newEnrollment.setEnrollmentDateTime(DateTime.now());
        newEnrollment.setActive(true);
        service.rollover(patientId, DateTime.now());
        verifyCreateNewEnrollment(newEnrollment, allCampaigns.nextCycleDateFromToday(newEnrollment.getServiceType(), medium));
    }

    private void verifyCreateNewEnrollment(MobileMidwifeEnrollment expected, LocalDate expectedScheduleStartDate) {
        assertCampaignSchedule(expected.createCampaignRequestForTextMessage(expectedScheduleStartDate));
        assertEnrollment(expected, allEnrollments.findActiveBy(expected.getPatientId()));
    }

    protected void assertCampaignSchedule(CampaignRequest campaignRequest) {

        String externalId = campaignRequest.externalId();
        String campaignName = campaignRequest.campaignName();
        CampaignEnrollmentsQuery campaignEnrollmentQuery = new CampaignEnrollmentsQuery().withExternalId(externalId).withCampaignName(campaignName).havingState(CampaignEnrollmentStatus.ACTIVE);
        CampaignEnrollmentRecord campaignEnrollmentRecord = messageCampaignService.search(campaignEnrollmentQuery).get(0);
        assertThat(campaignEnrollmentRecord.getStartDate(), is(campaignRequest.referenceDate()));
        assertThat((String) ReflectionTestUtils.getField(campaignEnrollmentRecord, "externalId"), is(campaignRequest.externalId()));
        assertThat((String) ReflectionTestUtils.getField(campaignEnrollmentRecord, "campaignName"), is(campaignRequest.campaignName()));

        String messageKey = getMessageKey(campaignName);
        String jobId = String.format("%s-MessageJob.%s.%s.%s", INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, campaignName, externalId, messageKey);
        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");
            assertNotNull(cronTrigger.getCronExpression());
            JobDataMap map = jobDetail.getJobDataMap();
            assertThat(map.get(EventKeys.EXTERNAL_ID_KEY).toString(), is(externalId));
            assertThat(map.get(EventKeys.CAMPAIGN_NAME_KEY).toString(), is(campaignName));
            assertThat(map.get("eventType").toString(), is(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT));
        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    private String getMessageKey(String campaignName) {
        CampaignMessage message = allMessageCampaigns.getCampaignMessageByMessageName(campaignName, ServiceType.valueOf(campaignName).getServiceName(Medium.SMS));
        return message.messageKey();
    }
}
