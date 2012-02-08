package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

import static junit.framework.Assert.*;
import static org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollmentTest.assertEnrollment;

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
                .reasonToJoin(ReasonToJoin.FAMILY_FRIEND_DELIVERED)
                .active(true).build();
        MobileMidwifeEnrollment expired = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Friday).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(Medium.SMS).
                messageStartWeek("pregnancy week17").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(false).build();
        allEnrollments.add(expected);
        allEnrollments.add(expired);
        MobileMidwifeEnrollment actualEnrollment = allEnrollments.findActiveBy(patientId);
        assertEnrollment(expected, actualEnrollment);
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
    public void shouldFindLatestEnrollment() {
        Date date = new Date(2001, 9, 9);
        String patientId = "131612";
        MobileMidwifeEnrollment enrollment1 = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Monday).active(true).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(Medium.SMS).enrollmentDateTime(DateUtil.newDateTime(date))
                .messageStartWeek("pregnancy week17").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        MobileMidwifeEnrollment enrollment2 = new MobileMidwifeEnrollmentBuilder().patientId(patientId).
                facilityId("23435").staffId("1234").consent(true).dayOfWeek(DayOfWeek.Monday).active(false).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.KAS).medium(Medium.SMS).enrollmentDateTime(DateUtil.now())
                .messageStartWeek("pregnancy week17").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .build();

        allEnrollments.add(enrollment1);
        allEnrollments.add(enrollment2);
        MobileMidwifeEnrollment actualEnrollment = allEnrollments.findLatestEnrollment(patientId);
        assertEnrollment(enrollment2, actualEnrollment);
    }

    @After
    public void clearAllDocuments() {
        allEnrollments.removeAll();
    }
}
