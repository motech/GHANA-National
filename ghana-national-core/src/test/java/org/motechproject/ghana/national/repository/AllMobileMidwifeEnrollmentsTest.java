package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

public class AllMobileMidwifeEnrollmentsTest extends BaseIntegrationTest {

    @Autowired
    private AllMobileMidwifeEnrollments allEnrollments;

    @Test
    public void shouldFindEnrollmentsByPatientId() {
        String patientId = "1234567";
        MobileMidwifeEnrollment expected = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(Medium.SMS).
                messageStartWeek("pregnancy week17").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        MobileMidwifeEnrollment expired = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(Medium.SMS).
                messageStartWeek("pregnancy week17").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(false).build();
        allEnrollments.add(expected);
        allEnrollments.add(expired);
        MobileMidwifeEnrollment actualEnrollment = allEnrollments.findByPatientId(patientId);
        assertMobileMidwifeEnrollment(expected, actualEnrollment);
    }

    @Test
    public void shouldCreateEnrollmentIfNotExisting() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId("patientId").
                facilityId("23435").staffId("1234").build();
        assertNull(enrollment.getId());
        allEnrollments.createOrUpdate(enrollment);
        assertNotNull(enrollment.getId());
    }

    @Test
    public void shouldUpdateEnrollmentIfExisting() {
        MobileMidwifeEnrollment createdEnrollment = new MobileMidwifeEnrollmentBuilder().patientId("patientId").
                facilityId("23435").staffId("1234").build();
        allEnrollments.add(createdEnrollment);

        MobileMidwifeEnrollment enrollmentToUpdate = allEnrollments.get(createdEnrollment.getId());
        String updatedFacilityId = "111111";
        enrollmentToUpdate.setFacilityId(updatedFacilityId);

        allEnrollments.createOrUpdate(enrollmentToUpdate);
        
        MobileMidwifeEnrollment updatedEnrollment = allEnrollments.get(createdEnrollment.getId());
        assertEquals(updatedFacilityId, updatedEnrollment.getFacilityId());
    }

    @Test
    public void shouldCreateCampaignRequestFromEnrollment() {
        ServiceType serviceType = ServiceType.PREGNANCY;
        String patientId = "patientId";
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType);
        CampaignRequest campaignRequest = mobileMidwifeEnrollment.createCampaignRequest();
        assertThat(campaignRequest.campaignName(), is(serviceType.name()));
        assertThat(campaignRequest.externalId(), is(patientId));
        assertThat(campaignRequest.referenceDate(), is(DateUtil.today()));
    }

    private void assertMobileMidwifeEnrollment(MobileMidwifeEnrollment expected, MobileMidwifeEnrollment actual) {
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
    }

    @After
    public void clearAllDocuments() {
        allEnrollments.removeAll();
    }
}
