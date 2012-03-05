package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

public class PatientTest extends BaseUnitTest {

    DateTime todayAs6June2012;
    @Before
    public void setUp() {
     todayAs6June2012  = new DateTime(2012, 6, 5, 0, 0);
     mockCurrentDate(todayAs6June2012);
    }

    @Test
    public void shouldReturnPatientCareForDeliveryFromExpectedDeliveryDate() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(28).plusDays(6).toLocalDate());

        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery());
        assertPatientCare(patientCares.get(0), ANC_DELIVERY, pregnancy.dateOfConception());
    }

    @Test
    public void shouldReturnPatientCareForIPTpForIfCurrentPregnancyWeekIsOnOrBeforeWeek19() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(28).plusDays(6).toLocalDate());
        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery());

        assertThat(patientCares, hasItem(new PatientCare(ANC_IPT_VACCINE, pregnancy.dateOfConception())));
    }

    @Test
    public void shouldReturnPatientCareForIPTiForIfCurrentDOBIsOnOrBeforeWeek14() {

        LocalDate birthDay = todayAs6June2012.minusWeeks(14).plusDays(1).toLocalDate();
        List<PatientCare> patientCares = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(birthDay.toDate()), null))
                .cwcCareProgramToEnrollOnRegistration();
        assertThat(patientCares, hasItem(new PatientCare(CWC_IPT_VACCINE, birthDay)));

        birthDay = todayAs6June2012.minusWeeks(14).toLocalDate();
        patientCares = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(birthDay.toDate()), null))
                .cwcCareProgramToEnrollOnRegistration();
        assertThat(patientCares, not(hasItem(new PatientCare(CWC_IPT_VACCINE, birthDay))));
    }

    @Test
    public void shouldNotReturnPatientCareForIPTpForIfCurrentPregnancyWeekIsAfterWeek13() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(12).plusDays(6).toLocalDate());
        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery());

        assertEquals(1, patientCares.size());
        assertThat(patientCares, hasItem(new PatientCare(ANC_DELIVERY, pregnancy.dateOfConception())));
    }
    
    @Test
    public void shouldReturnAllCWCCareProgramsApplicableDuringRegistration() {
        LocalDate dateOfBirth = todayAs6June2012.minusMonths(1).toLocalDate();
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(dateOfBirth.toDate()), null));
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnRegistration();
        assertThat(patientCares, hasItem(new PatientCare(CWC_BCG, dateOfBirth)));
        assertThat(patientCares, hasItem(new PatientCare(CWC_YELLOW_FEVER, dateOfBirth)));
        assertThat(patientCares, hasItem(new PatientCare(CWC_PENTA, dateOfBirth)));
        assertThat(patientCares, hasItem(new PatientCare(CWC_MEASLES_VACCINE, dateOfBirth)));
        assertThat(patientCares, hasItem(new PatientCare(CWC_IPT_VACCINE, dateOfBirth)));
    }
    
    @Test
    public void shouldNotReturnMeaslesPatientCareForCWCRegistration_IfAgeIsMoreThanAYear() {
        LocalDate dateOfBirthOneYearBack = todayAs6June2012.minusYears(5).toLocalDate();
        Patient patient = new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(dateOfBirthOneYearBack.toDate()), null));
        assertThat(patient.cwcCareProgramToEnrollOnRegistration(), not(hasItem(new PatientCare(CWC_MEASLES_VACCINE, dateOfBirthOneYearBack))));
    }

    private void assertPatientCare(PatientCare patientCare, String name, LocalDate startingOn) {
        assertThat(patientCare.name(), is(name));
        assertThat(patientCare.startingOn(), is(startingOn));
    }
}
