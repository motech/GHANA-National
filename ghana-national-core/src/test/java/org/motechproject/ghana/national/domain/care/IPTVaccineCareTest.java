package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.today;

public class IPTVaccineCareTest {

   @Before
    public void setUp(){
        initMocks(this);
    }

    @Test
    public void shouldReturnHistoryPatientCareWithNextMilestone_IfHistoryIsProvidedAndNoActiveScheduleExists() {

        final String facilityId = "fid";
        LocalDate enrollmentDate = newDate(2012, 3, 3);
        LocalDate lastIPTVaccinationDate = newDate(2012, 1, 1);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(14));
        boolean hasActiveIPTSchedule=false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithIPTDependent(enrollmentDate, lastIPTVaccinationDate, 1.0);

        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), activePregnancyObs,hasActiveIPTSchedule).care();

        PatientCare expectedPatientCare = PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.ANC_IPT_VACCINE, lastIPTVaccinationDate, "IPT2", new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldReturnPatientCareWithDefaultStartMilestoneIfNoHistoryIsProvidedDuringRegistrationAndNoActiveScheduleExists() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(18));
        boolean hasActiveIPTSchedule=false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutIPT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), activePregnancyObsWithoutIPT,hasActiveIPTSchedule).care();

        PatientCare expectedPatientCare = PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_IPT_VACCINE, pregnancy.dateOfConception(), new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldNotReturnPatientCarIfIrrelevantOrNoHistoryIsProvidedDuringCareHistoryFormUploadAndNoActiveScheduleExists() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(18));
        boolean hasActiveIPTSchedule=false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutIPT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), activePregnancyObsWithoutIPT,hasActiveIPTSchedule).careForHistory();

        assertNull(patientCare);
    }

    @Test
    public void shouldNotReturnPatientCareIfNoHistoryAndNoActiveEnrollmentAndNotApplicableForTheCurrentRegistration() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";
        LocalDate conceptionDate20WeekInPast = today().minusWeeks(20);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate20WeekInPast);
        boolean hasActiveIPTSchedule=false;

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutIPT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), activePregnancyObsWithoutIPT,hasActiveIPTSchedule).care();

        assertNull(patientCare);
    }

    @Test
    public void shouldNotReturnPatientCareIfThereIsAnActiveIPTEnrollment() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";
        LocalDate conceptionDate20WeekInPast = today().minusWeeks(20);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate20WeekInPast);

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutIPT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        boolean hasActiveIPTSchedule=true;
        PatientCare patientCare = new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), activePregnancyObsWithoutIPT,hasActiveIPTSchedule).care();

        assertNull(patientCare);
    }

    @Test
    public void shouldNotCreatePatientCareIfHistoryProvidedIsTheLastMilestone(){
       LocalDate enrollmentDate = today();
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(enrollmentDate.minusMonths(9));
        LocalDate lastIPTVaccinationDate=pregnancy.dateOfConception().plusMonths(6);


        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithIPTDependent(enrollmentDate, lastIPTVaccinationDate, 3.0);
        PatientCare patientCare = new IPTVaccineCare(patient, enrollmentDate, activePregnancyObs,false).care();

        assertNull(patientCare);
    }

    private MRSObservation<String> createPregnacyObservationWithIPTDependent(LocalDate enrollmentDate, LocalDate iptVaccinationDate, Double iptDose) {
        MRSObservation<String> activePregnancyObs = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        activePregnancyObs.addDependantObservation(new MRSObservation<Double>(iptVaccinationDate.toDate(), Concept.IPT.getName(), iptDose));
        return activePregnancyObs;
    }

    private <T> T getField(Object object, String fieldName) {
        return (T) ReflectionTestUtils.getField(object, fieldName);
    }
}
