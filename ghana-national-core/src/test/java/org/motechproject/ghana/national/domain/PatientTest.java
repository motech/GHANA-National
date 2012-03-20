package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

public class PatientTest extends BaseUnitTest {

    DateTime todayAs6June2012;

    @Before
    public void setUp() {
        todayAs6June2012 = new DateTime(2012, 6, 5, 20, 10);
        mockCurrentDate(todayAs6June2012);
    }

    @Test
    public void shouldReturnPatientCareForDeliveryFromExpectedDeliveryDate() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(28).plusDays(6).toLocalDate());

        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery(), todayAs6June2012.toLocalDate(), noANCHistory(), new ActiveCareSchedules());
        assertPatientCare(patientCares.get(0), patientCare(ANC_DELIVERY, pregnancy.dateOfConception(), todayAs6June2012.toLocalDate()));
    }

    @Test
    public void shouldReturnPatientCareForIPTpForIfCurrentPregnancyWeekIsOnOrBeforeWeek19() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(28).plusDays(6).toLocalDate());
        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery(), todayAs6June2012.toLocalDate(), noANCHistory(), new ActiveCareSchedules());

        assertThat(patientCares, hasItem(new PatientCare(ANC_IPT_VACCINE, pregnancy.dateOfConception(), todayAs6June2012.toLocalDate())));
    }

    @Test
    public void shouldReturnPatientCareForIPTiForIfCurrentDOBIsOnOrBeforeWeek14() {

        LocalDate birthDay = todayAs6June2012.minusWeeks(14).plusDays(1).toLocalDate();
        List<PatientCare> patientCares = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(birthDay.toDate()), null))
                .cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate());
        assertThat(patientCares, hasItem(new PatientCare(CWC_IPT_VACCINE, birthDay, todayAs6June2012.toLocalDate())));

        birthDay = todayAs6June2012.minusWeeks(14).toLocalDate();
        patientCares = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(birthDay.toDate()), null))
                .cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate());
        assertThat(patientCares, not(hasItem(new PatientCare(CWC_IPT_VACCINE, birthDay, todayAs6June2012.toLocalDate()))));
    }

    @Test
    public void shouldNotReturnPatientCareForIPTpForIfCurrentPregnancyWeekIsAfterWeek13() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(12).plusDays(6).toLocalDate());
        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery(), todayAs6June2012.toLocalDate(), noANCHistory(), new ActiveCareSchedules());

        assertThat(patientCares, not(hasItem(new PatientCare(ScheduleNames.ANC_IPT_VACCINE, todayAs6June2012.toLocalDate(), todayAs6June2012.toLocalDate()))));
    }

    @Test
    public void shouldReturnAllCWCCareProgramsApplicableDuringRegistration() {
        LocalDate dateOfBirth = todayAs6June2012.minusMonths(1).toLocalDate();
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(dateOfBirth.toDate()), null));
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate());
        assertPatientCares(patientCares, asList(new PatientCare(CWC_BCG, dateOfBirth, todayAs6June2012.toLocalDate()),
                new PatientCare(CWC_YELLOW_FEVER, dateOfBirth, todayAs6June2012.toLocalDate()),
                new PatientCare(CWC_PENTA, dateOfBirth, todayAs6June2012.toLocalDate()),
                new PatientCare(CWC_MEASLES_VACCINE, dateOfBirth, todayAs6June2012.toLocalDate()),
                new PatientCare(CWC_IPT_VACCINE, dateOfBirth, todayAs6June2012.toLocalDate()),
                new PatientCare(CWC_OPV_0, dateOfBirth, todayAs6June2012.toLocalDate()),
                new PatientCare(CWC_OPV_OTHERS, dateOfBirth, todayAs6June2012.toLocalDate())));
    }

    @Test
    public void shouldNotReturnMeaslesPatientCareForCWCRegistration_IfAgeIsMoreThanAYear() {
        LocalDate dateOfBirth5YearBack = todayAs6June2012.minusYears(5).toLocalDate();
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(dateOfBirth5YearBack.toDate()), null));
        assertThat(patient.cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate()), not(hasItem(new PatientCare(CWC_MEASLES_VACCINE, dateOfBirth5YearBack, todayAs6June2012.toLocalDate()))));
    }

    @Test
    public void shouldReturnPatientCaresForPNCChildProgram() {
        DateTime dateOfBirth = todayAs6June2012.minusDays(5);
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(dateOfBirth.toDate()), null));
        List<PatientCare> cares = patient.pncBabyProgramsToEnrollOnRegistration();
        assertPatientCares(cares, asList(
                new PatientCare(PNC_CHILD_1, dateOfBirth, dateOfBirth),
                new PatientCare(PNC_CHILD_2, dateOfBirth, dateOfBirth),
                new PatientCare(PNC_CHILD_3, dateOfBirth, dateOfBirth)));
    }

    @Test
    public void shouldReturnPatientCaresForPNCMotherProgram() {
        DateTime deliveryDate = todayAs6June2012.minusDays(10);
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson(), null));
        List<PatientCare> cares = patient.pncMotherProgramsToEnrollOnRegistration(deliveryDate);
        assertPatientCares(cares, asList(
                new PatientCare(PNC_MOTHER_1, deliveryDate, deliveryDate),
                new PatientCare(PNC_MOTHER_2, deliveryDate, deliveryDate),
                new PatientCare(PNC_MOTHER_3, deliveryDate, deliveryDate)));
    }

    @Test
    public void shouldReturnPatientCareForTTVaccineIfNoActiveScheduleAndNoHistoryIsPresent() {
        LocalDate registrationDate = todayAs6June2012.toLocalDate();
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson(), null));

        EnrollmentRecord ttEnrollmentRecord = mock(EnrollmentRecord.class);
        List<PatientCare> patientCares = patient.ancCareProgramsToEnrollOnRegistration(DateUtil.newDate(2000, 1, 1), registrationDate, null, new ActiveCareSchedules().setActiveCareSchedule(TT_VACCINATION, ttEnrollmentRecord));
        assertThat(patientCares, not(hasItem(new PatientCare(TT_VACCINATION, registrationDate, registrationDate))));

        patientCares = patient.ancCareProgramsToEnrollOnRegistration(DateUtil.newDate(2000, 1, 1), registrationDate, null, new ActiveCareSchedules());
        assertThat(patientCares, hasItem(new PatientCare(TT_VACCINATION, registrationDate, registrationDate)));
    }

    private void assertPatientCare(PatientCare patientCare, PatientCare expected) {
        assertThat(patientCare.name(), is(expected.name()));
        assertThat(patientCare.startingOn(), is(expected.startingOn()));
        assertThat(patientCare.referenceTime(), is(expected.referenceTime()));
        assertThat(patientCare.enrollmentDate(), is(expected.enrollmentDate()));
        assertThat(patientCare.enrollmentTime(), is(expected.enrollmentTime()));
        assertThat(patientCare.preferredTime(), is(expected.preferredTime()));
    }

    private void assertPatientCares(List<PatientCare> actualList, List<PatientCare> expectedList) {
        assertThat(actualList.size(), is(expectedList.size()));
        for (PatientCare expectedCare : expectedList)
            assertThat("Missing " + expectedCare.name(), actualList, hasItem(expectedCare));
    }

    private PatientCare patientCare(String name, LocalDate reference, LocalDate enrollmentDate) {
        return new PatientCare(name, reference, enrollmentDate);
    }

    private PatientCare patientCare(String name, DateTime reference, DateTime enrollmentDate) {
        return new PatientCare(name, reference, enrollmentDate);
    }

    private ANCCareHistoryVO noANCHistory() {
        return new ANCCareHistoryVO(false, new ArrayList<ANCCareHistory>(), null, null, null, null);
    }
}
