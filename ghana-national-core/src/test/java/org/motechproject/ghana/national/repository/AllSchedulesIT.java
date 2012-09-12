package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.configuration.BaseScheduleTrackingTest;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.PatientTest;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.domain.TestSchedule;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.Period.weeks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.domain.Patient.FACILITY_META;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class AllSchedulesIT extends BaseScheduleTrackingTest {
    @Autowired
    private AllCareSchedules allCareSchedules;
    @Autowired
    private TestSchedule testSchedule;

    @Test
    public void shouldGetUpcomingAlertsForPatientInTheNext7Days() {
        final String externalId = "patientId";

        final LocalDate today = newDate("25-Jan-2012");
        mockCurrentDate(today);
        final LocalDate dateOfConception = basedOnDeliveryDate(newDate("22-SEP-2012")).dateOfConception();

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest().setExternalId(externalId)
                .setScheduleName(ANC_IPT_VACCINE.getName()).setPreferredAlertTime(new Time(10, 10))
                .setReferenceDate(dateOfConception).setEnrollmentDate(dateOfConception);
        scheduleTrackingService.enroll(enrollmentRequest);

        mockCurrentDate(newDate("17-MAR-2012"));
        assertEquals(0, allCareSchedules.upcomingCareForCurrentWeek(externalId).size());

        mockCurrentDate(newDateWithTime("18-MAR-2012", "00:00:00"));
        List<EnrollmentRecord> enrollments = allCareSchedules.upcomingCareForCurrentWeek(externalId);
        assertEquals(1, enrollments.size());
        assertThat(enrollments.get(0).getStartOfDueWindow(), is(newDateTime(2012, 3, 24, 0, 0, 0)));

        mockCurrentDate(newDateWithTime("24-MAR-2012", "23:59:59.999"));
        assertEquals(1, allCareSchedules.upcomingCareForCurrentWeek(externalId).size());

        mockCurrentDate(newDateWithTime("25-MAR-2012", "00:00:00"));
        assertEquals(0, allCareSchedules.upcomingCareForCurrentWeek(externalId).size());
    }

    @Test
    public void shouldGetDefaultersListByFacility() {

         String facilityId = "facilityId";
         String facilityId2 = "facilityId2";
         final String externalId = "patientId";
         final String externalId2 = "patientId2";
         final String externalId3 = "patientId3";

         final LocalDate patient1ConceptionFallsInLate = testSchedule.referenceDateToStartInDueWindow(ANC_IPT_VACCINE.getName());
         final LocalDate patient2ConceptionFallsInDue = testSchedule.referenceDateToStartInLateWindow(ANC_IPT_VACCINE.getName());
         final LocalDate patient3ConceptionFallsInDue = testSchedule.referenceDateToStartInLateWindow(ANC_IPT_VACCINE.getName(), weeks(2));

         scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId(externalId).setScheduleName(ANC_IPT_VACCINE.getName())
                 .setPreferredAlertTime(new Time(10, 10)).setReferenceDate(patient1ConceptionFallsInLate)
                 .setEnrollmentDate(patient1ConceptionFallsInLate).setMetadata(PatientTest.facilityMetaData(facilityId)));
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId(externalId2).setScheduleName(ANC_IPT_VACCINE.getName())
                .setPreferredAlertTime(new Time(10, 10)).setReferenceDate(patient2ConceptionFallsInDue)
                .setEnrollmentDate(patient2ConceptionFallsInDue).setMetadata(PatientTest.facilityMetaData(facilityId)));
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId(externalId3).setScheduleName(ANC_IPT_VACCINE.getName())
                .setPreferredAlertTime(new Time(10, 10)).setReferenceDate(patient3ConceptionFallsInDue)
                .setEnrollmentDate(patient3ConceptionFallsInDue).setMetadata(PatientTest.facilityMetaData(facilityId2)));


        List<EnrollmentRecord> enrollmentRecords = allCareSchedules.defaultersByMetaSearch(FACILITY_META, facilityId);
         assertThat(extract(enrollmentRecords, on(EnrollmentRecord.class).getExternalId()), is(asList(externalId2)));

         enrollmentRecords = allCareSchedules.defaultersByMetaSearch(FACILITY_META, facilityId2);
         assertThat(extract(enrollmentRecords, on(EnrollmentRecord.class).getExternalId()), is(asList(externalId3)));
     }

    @Test
    public void shouldEnrollOnlyIfThereIsNoActiveEnrollment() {
        String externalId1 = "externalId";
        EnrollmentRequest enrollmentRequestFor1stMilestone = new EnrollmentRequest().setExternalId(externalId1).setScheduleName(ScheduleNames.TT_VACCINATION.getName()).setReferenceDate(DateUtil.today());
        String externalId2 = "externalId2";
        EnrollmentRequest existingEnrollmentFor2ndMilestone = new EnrollmentRequest().setExternalId(externalId2)
                .setScheduleName(ScheduleNames.TT_VACCINATION.getName()).setReferenceDate(DateUtil.today())
                .setStartingMilestoneName(TTVaccineDosage.TT2.getScheduleMilestoneName());

        allCareSchedules.enroll(existingEnrollmentFor2ndMilestone);

        assertNull(allCareSchedules.getActiveEnrollment(externalId1, TT_VACCINATION.getName()));
        assertNotNull(allCareSchedules.getActiveEnrollment(externalId2, TT_VACCINATION.getName()));

        assertTrue(allCareSchedules.enrollIfNotActive(enrollmentRequestFor1stMilestone));
        assertFalse(allCareSchedules.enrollIfNotActive(existingEnrollmentFor2ndMilestone));
    }
}
