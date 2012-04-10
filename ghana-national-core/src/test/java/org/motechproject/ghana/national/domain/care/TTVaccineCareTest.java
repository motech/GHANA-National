package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;

import java.util.HashMap;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.today;

public class TTVaccineCareTest {

    @Test
    public void shouldNotCreateScheduleIfActiveScheduleAlreadyExists() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";
        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutTT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);

        boolean hasActiveTTSchedule=true;

        PatientCare patientCare = new TTVaccineCare(patient, enrollmentDate, activePregnancyObsWithoutTT, hasActiveTTSchedule).careForANCReg();

        assertNull(patientCare);
    }

    @Test
    public void shouldReturnHistoryPatientCareWithNextMilestone_IfActiveScheduleNotExistsAndHistoryIsProvided() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        LocalDate ttVaccinationDate = newDate(2012, 1, 1);
        final String facilityId = "fid";

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        Double ttDose = 2.0;
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithTTDependent(enrollmentDate, ttVaccinationDate, ttDose);
        boolean hasActiveTTSchedule=false;

        PatientCare patientCare = new TTVaccineCare(patient, enrollmentDate, activePregnancyObs, hasActiveTTSchedule).careForANCReg();

        PatientCare expectedPatientCare = PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.TT_VACCINATION, ttVaccinationDate, TTVaccineDosage.TT3.name(), new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldReturnPatientCareWithDefaultStartMilestoneIfNoActiveScheduleExistsAndIrrelevantOrNoHistoryIsProvidedDuringRegistration() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutTT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        boolean hasActiveTTSchedule=false;

        PatientCare patientCare = new TTVaccineCare(patient, enrollmentDate, activePregnancyObsWithoutTT, hasActiveTTSchedule).careForANCReg();

        PatientCare expectedPatientCare = PatientCare.forEnrollmentFromStart(ScheduleNames.TT_VACCINATION, enrollmentDate, new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldNotReturnPatientCareIfNoActiveScheduleExistsAndIrrelevantOrNoHistoryIsProvidedDuringCareHistoryFormUpload() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutTT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        boolean hasActiveTTSchedule=false;

        PatientCare patientCare = new TTVaccineCare(patient, enrollmentDate, activePregnancyObsWithoutTT, hasActiveTTSchedule).careForHistory();
        assertNull(patientCare);
    }

    @Test
    public void shouldNotCreatePatientCareIfHistoryProvidedIsTheLastMilestone() {
        LocalDate enrollmentDate = today();
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(enrollmentDate.minusMonths(9));
        LocalDate lastTTVaccinationDate = pregnancy.dateOfConception().plusMonths(6);
        boolean hasActiveTTSchedule=false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithTTDependent(enrollmentDate, lastTTVaccinationDate, 5.0);
        PatientCare patientCare = new TTVaccineCare(patient, enrollmentDate, activePregnancyObs, hasActiveTTSchedule).careForANCReg();

        assertNull(patientCare);
    }

    private MRSObservation<String> createPregnacyObservationWithTTDependent(LocalDate enrollmentDate, LocalDate ttVaccinationDate, Double ttDose) {
        MRSObservation<String> activePregnancyObs = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        activePregnancyObs.addDependantObservation(new MRSObservation<Double>(ttVaccinationDate.toDate(), Concept.TT.getName(), ttDose));
        return activePregnancyObs;
    }

}
