package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.model.DayOfWeek;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
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
    @Autowired
    private AllPatientsOutbox allPatientsOutbox;
    @Autowired
    private VoiceOutboxService voiceOutboxService;
    @Test
    public void shouldCreateMobileMidwifeEnrollmentAndCreateScheduleIfNotRegisteredAlready() {

        Medium medium = Medium.SMS;
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId(identifierGenerator.newPatientId()).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).serviceType(ServiceType.PREGNANCY).
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
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).serviceType(ServiceType.PREGNANCY).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(medium).reasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH).
                messageStartWeek("20").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        service.register(enrollment);
        MobileMidwifeEnrollment newEnrollment = MobileMidwifeEnrollment.cloneNew(enrollment).setMessageStartWeek("41").setServiceType(ServiceType.CHILD_CARE);
        newEnrollment.setEnrollmentDateTime(DateTime.now());
        newEnrollment.setActive(true);
        service.rollover(patientId, DateTime.now());
        verifyCreateNewEnrollment(newEnrollment, allCampaigns.nextCycleDateFromToday(newEnrollment.getServiceType(), medium));
    }
    
    @Test
    public void shouldRemoveMessagesFromOutboxOnUnregister() {
        String patientId = identifierGenerator.newPatientId();
        Medium medium = Medium.VOICE;
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).serviceType(ServiceType.PREGNANCY).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(medium).reasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH).
                messageStartWeek("20").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        service.register(enrollment);
        allPatientsOutbox.addMobileMidwifeMessage(patientId, MobileMidwifeAudioClips.PREGNANCY_WEEK_7, Period.days(7));
        allPatientsOutbox.addCareMessage(patientId, "care_clip", Period.days(7), AlertWindow.DUE, DateTime.now());
        service.unRegister(patientId);
        List<OutboundVoiceMessage> remainingMessages = voiceOutboxService.getMessages(patientId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        assertEquals(1, remainingMessages.size());
        assertFalse(remainingMessages.get(0).getParameters().get("TYPE").equals(AlertType.MOBILE_MIDWIFE));
        assertTrue(remainingMessages.get(0).getParameters().get("AUDIO_CLIP_NAME").equals("care_clip"));
    }

    private void verifyCreateNewEnrollment(MobileMidwifeEnrollment expected, LocalDate expectedScheduleStartDate) {
        assertCampaignSchedule(expected.createCampaignRequestForTextMessage(expectedScheduleStartDate),expected.getServiceType());
        assertEnrollment(expected, allEnrollments.findActiveBy(expected.getPatientId()));
    }

    protected void assertCampaignSchedule(CampaignRequest campaignRequest,ServiceType serviceType) {
        String externalId = campaignRequest.externalId();
        String campaignName = campaignRequest.campaignName();
        CampaignEnrollmentsQuery campaignEnrollmentQuery = new CampaignEnrollmentsQuery().withExternalId(externalId).withCampaignName(campaignName).havingState(CampaignEnrollmentStatus.ACTIVE);
        CampaignEnrollmentRecord campaignEnrollmentRecord = messageCampaignService.search(campaignEnrollmentQuery).get(0);
        assertThat(campaignEnrollmentRecord.getStartDate(), is(campaignRequest.referenceDate()));
        assertThat((String) ReflectionTestUtils.getField(campaignEnrollmentRecord, "externalId"), is(campaignRequest.externalId()));
        assertThat((String) ReflectionTestUtils.getField(campaignEnrollmentRecord, "campaignName"), is(campaignRequest.campaignName()));

        String messageKey = getMessageKey(campaignName,serviceType);
        String jobId = String.format("%s-MessageJob.%s.%s.%s", INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, campaignName, externalId, messageKey);
        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(new JobKey(jobId, "default"));
            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(new TriggerKey(jobId, "default"));
            assertNotNull(cronTrigger.getCronExpression());
            JobDataMap map = jobDetail.getJobDataMap();
            assertThat(map.get(EventKeys.EXTERNAL_ID_KEY).toString(), is(externalId));
            assertThat(map.get(EventKeys.CAMPAIGN_NAME_KEY).toString(), is(campaignName));
            assertThat(map.get("eventType").toString(), is(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT));
        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    private String getMessageKey(String campaignName, ServiceType serviceType) {
        CampaignMessage message = allMessageCampaigns.getCampaignMessageByMessageName(campaignName, serviceType.getServiceName(Medium.SMS));
        return message.messageKey();
    }
}
