package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;

import java.util.HashMap;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.today;

public class VaccineCareTest {

    @Test
    public void shouldReturnHistoryPatientCareWithNextMilestone_IfHistoryIsProvidedAndNoActiveScheduleExists() {

        final String facilityId = "fid";
        LocalDate lastIPTVaccinationDate = newDate(2012, 1, 1);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(14));
        boolean hasActiveIPTSchedule = false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));

        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), hasActiveIPTSchedule, "1", lastIPTVaccinationDate.toDate(), true).careForANCReg();

        PatientCare expectedPatientCare = PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.ANC_IPT_VACCINE, lastIPTVaccinationDate, "IPT2", new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldReturnPatientCareWithDefaultStartMilestoneIfNoHistoryIsProvidedDuringRegistrationAndNoActiveScheduleExists() {

        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(18));
        boolean hasActiveIPTSchedule = false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), hasActiveIPTSchedule, null, null, false).careForANCReg();

        PatientCare expectedPatientCare = PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_IPT_VACCINE, pregnancy.dateOfConception(), new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldNotReturnPatientCarIfIrrelevantOrNoHistoryIsProvidedDuringCareHistoryFormUploadAndNoActiveScheduleExists() {

        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(18));
        boolean hasActiveIPTSchedule = false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), hasActiveIPTSchedule, null, null, false).careForHistory();

        assertNull(patientCare);
    }

    @Test
    public void shouldNotReturnPatientCareIfNoHistoryAndNoActiveEnrollmentAndNotApplicableForTheCurrentRegistration() {

        final String facilityId = "fid";
        LocalDate conceptionDate20WeekInPast = today().minusWeeks(20);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate20WeekInPast);
        boolean hasActiveIPTSchedule = false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), hasActiveIPTSchedule, null, null, false).careForANCReg();

        assertNull(patientCare);
    }

    @Test
    public void shouldNotReturnPatientCareIfThereIsAnActiveIPTEnrollment() {

        final String facilityId = "fid";
        LocalDate conceptionDate20WeekInPast = today().minusWeeks(20);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate20WeekInPast);

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        boolean hasActiveIPTSchedule = true;
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), hasActiveIPTSchedule, null, null, false).careForANCReg();

        assertNull(patientCare);
    }

    @Test
    public void shouldNotCreatePatientCareIfHistoryProvidedIsTheLastMilestone() {
        LocalDate enrollmentDate = today();
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(enrollmentDate.minusMonths(9));
        LocalDate lastIPTVaccinationDate = pregnancy.dateOfConception().plusMonths(6);


        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        PatientCare patientCare = new IPTVaccineCare(patient, enrollmentDate, false, "3", lastIPTVaccinationDate.toDate(), true).careForANCReg();

        assertNull(patientCare);
    }

}
