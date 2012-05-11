package org.motechproject.ghana.national.domain.mobilemidwife;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment.newEnrollment;

public class MobileMidwifeEnrollmentTest {

    @Test
    public void shouldCreateCampaignRequestFromEnrollment() {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "patientId";
        String messageStartWeekKey = "55";
        LocalDate cycleStartDate = new LocalDate();
        final DateTime scheduleStartDate = cycleStartDate.toDateTime(LocalTime.now());
        MobileMidwifeEnrollment mobileMidwifeEnrollment = newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMessageStartWeek(messageStartWeekKey);

        CampaignRequest campaignRequest = mobileMidwifeEnrollment.createCampaignRequest(scheduleStartDate.toLocalDate());
        assertThat(campaignRequest.campaignName(), is(serviceType.name()));
        assertThat(campaignRequest.externalId(), is(patientId));
        assertThat(campaignRequest.referenceDate(), is(DateUtil.today()));
        assertThat(campaignRequest.startOffset(), is(MessageStartWeek.findBy(messageStartWeekKey).getWeek()));

        assertNull(campaignRequest.reminderTime());
        assertThat(campaignRequest.referenceDate(), is(equalTo(cycleStartDate)));
    }

    @Test
    public void shouldCreateStopCampaignRequest() {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "patientId";

        MobileMidwifeEnrollment mobileMidwifeEnrollment = newEnrollment().setPatientId(patientId).setServiceType(serviceType);

        CampaignRequest stopRequest = mobileMidwifeEnrollment.stopCampaignRequest();
        assertThat(stopRequest.campaignName(), is(serviceType.name()));
        assertThat(stopRequest.externalId(), is(patientId));
        assertNull(stopRequest.referenceDate());
        assertNull(stopRequest.startOffset());
    }

    @Test
    public void shouldCheckIfCampaignApplicableForEnrollment() {
        assertTrue(newEnrollment().setConsent(true).setPhoneOwnership(PhoneOwnership.HOUSEHOLD).campaignApplicable());
        assertTrue(newEnrollment().setConsent(true).setPhoneOwnership(PhoneOwnership.PERSONAL).campaignApplicable());

        MobileMidwifeEnrollment enrollmentWithPublicOwnership = newEnrollment().setConsent(true).setPhoneOwnership(PhoneOwnership.PUBLIC);
        assertFalse(enrollmentWithPublicOwnership.campaignApplicable());

        assertFalse(newEnrollment().setConsent(false).campaignApplicable());
    }

    public static void assertEnrollment(MobileMidwifeEnrollment expected, MobileMidwifeEnrollment actual) {
        assertNotSame(expected, actual);
        assertEquals(expected.getPatientId(), actual.getPatientId());
        assertEquals(expected.getFacilityId(), actual.getFacilityId());
        assertEquals(expected.getStaffId(), actual.getStaffId());
        assertEquals(expected.getConsent(), actual.getConsent());
        assertEquals(expected.getDayOfWeek(), actual.getDayOfWeek());
        assertEquals(expected.getLearnedFrom(), actual.getLearnedFrom());
        assertEquals(expected.getLanguage(), actual.getLanguage());
        assertEquals(expected.getServiceType(), actual.getServiceType());
        assertEquals(expected.getPhoneOwnership(), actual.getPhoneOwnership());
        assertEquals(expected.getTimeOfDay(), actual.getTimeOfDay());
        assertEquals(expected.getMessageStartWeek(), actual.getMessageStartWeek());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(expected.getReasonToJoin(), actual.getReasonToJoin());
        assertEquals(expected.getActive(), actual.getActive());
        assertEquals(expected.getEnrollmentDateTime(), actual.getEnrollmentDateTime());
    }
}
