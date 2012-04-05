package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.IPTDose;
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
    public void shouldCreateIPTVaccineCareWithCorrectMilestoneGivenAnObservation() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        LocalDate iptVaccinationDate = newDate(2012, 1, 1);
        final String facilityId = "fid";

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithIPTDependent(enrollmentDate, iptVaccinationDate, 1.0);

        IPTVaccineCare vaccineCare = IPTVaccineCare.createFrom(patient, enrollmentDate, activePregnancyObs);

        PatientCare expectedPatientCare = PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.ANC_IPT_VACCINE, iptVaccinationDate, IPTDose.SP2.name(), new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(this.<PatientCare>getField(vaccineCare, "patientCareBasedOnHistory"), is(expectedPatientCare));
        assertThat(this.<Patient>getField(vaccineCare, "patient"), is(patient));
        assertThat(this.<LocalDate>getField(vaccineCare, "enrollmentDate"), is(enrollmentDate));
    }

    @Test
    public void shouldReturnHistoryPatientCareWithNextMilestone_IfHistoryIsProvided() {

        final String facilityId = "fid";
        LocalDate enrollmentDate = newDate(2012, 3, 3);
        LocalDate lastIPTVaccinationDate = newDate(2012, 1, 1);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(14));

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithIPTDependent(enrollmentDate, lastIPTVaccinationDate, 1.0);

        PatientCare patientCare = IPTVaccineCare.createFrom(patient, enrollmentDate, activePregnancyObs).care(pregnancy.dateOfDelivery());

        PatientCare expectedPatientCare = PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.ANC_IPT_VACCINE, lastIPTVaccinationDate, IPTDose.SP2.name(), new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldReturnPatientCareWithDefaultStartMilestoneIfNoHistoryIsProvided() {

        LocalDate enrollmentDate = newDate(2012, 3, 3);
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(today().minusWeeks(18));

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObsWithoutIPT = new MRSObservation<String>(enrollmentDate.toDate(), Concept.PREGNANCY.getName(), null);
        PatientCare patientCare = IPTVaccineCare.createFrom(patient, enrollmentDate, activePregnancyObsWithoutIPT).care(pregnancy.dateOfDelivery());

        PatientCare expectedPatientCare = PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_IPT_VACCINE, pregnancy.dateOfConception(), new HashMap<String, String>() {{
            put(Patient.FACILITY_META, facilityId);
        }});
        assertThat(patientCare, is(expectedPatientCare));
    }

    @Test
    public void shouldNotCreatePatientCareIfHistoryProvidedIsTheLastMilestone(){
       LocalDate enrollmentDate = today();
        final String facilityId = "fid";
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(enrollmentDate.minusMonths(9));
        LocalDate lastIPTVaccinationDate=pregnancy.dateOfConception().plusMonths(6);


        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        MRSObservation<String> activePregnancyObs = createPregnacyObservationWithIPTDependent(enrollmentDate, lastIPTVaccinationDate, 3.0);
        PatientCare patientCare = IPTVaccineCare.createFrom(patient, enrollmentDate, activePregnancyObs).care(pregnancy.dateOfDelivery());

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
