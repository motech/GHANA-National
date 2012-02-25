package org.motechproject.ghana.national.service;

import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
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
    private CampaignEnrollmentService campaignEnrollmentService;
    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Test
    public void shouldCreateMobileMidwifeEnrollmentAndCreateScheduleIfNotRegisteredAlready() {

        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId(identifierGenerator.newPatientId()).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).serviceType(ServiceType.PREGNANCY).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(Medium.SMS).reasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH).
                messageStartWeek("20").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        service.register(enrollment);
        verifyCreateNewEnrollment(enrollment);
    }

    private void verifyCreateNewEnrollment(MobileMidwifeEnrollment expected) {
        assertCampaignSchedule(expected.createCampaignRequest());
        assertEnrollment(expected, allEnrollments.findActiveBy(expected.getPatientId()));
    }

    protected void assertCampaignSchedule(CampaignRequest campaignRequest) {

        CampaignEnrollment campaignEnrollment = campaignEnrollmentService.findByExternalIdAndCampaignName(campaignRequest.externalId(), campaignRequest.campaignName());
        assertThat(campaignEnrollment.getStartDate(), is(campaignRequest.referenceDate()));
        assertThat((Integer) ReflectionTestUtils.getField(campaignEnrollment, "startOffset"), is(campaignRequest.startOffset()));

        String messageKey = getMessageKey(campaignRequest.campaignName());
        String jobId = String.format("%s-%s.%s.%s", INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, campaignRequest.campaignName(), campaignRequest.externalId(), messageKey);
        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");
            assertNotNull(cronTrigger.getCronExpression());
            JobDataMap map = jobDetail.getJobDataMap();
            assertThat(map.get(EventKeys.EXTERNAL_ID_KEY).toString(), is(campaignRequest.externalId()));
            assertThat(map.get(EventKeys.CAMPAIGN_NAME_KEY).toString(), is(campaignRequest.campaignName()));
            assertThat(map.get("eventType").toString(), is(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT));
        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    private String getMessageKey(String campaignName) {
        CampaignMessage message = allMessageCampaigns.getCampaignMessageByMessageName(campaignName, ServiceType.valueOf(campaignName).getServiceName());
        return message.messageKey();
    }
}
