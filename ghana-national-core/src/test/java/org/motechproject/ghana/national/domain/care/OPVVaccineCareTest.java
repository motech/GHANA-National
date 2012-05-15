package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_0;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_OTHERS;

public class OPVVaccineCareTest {

    @Test
    public void shouldReturnNextDosageGivenADosage() {
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(9);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility("facilityId")));
        OPVVaccineCare opvVaccineCare = new OPVVaccineCare(patient, null, null, null, null, CWC_OPV_0.getName());
        assertThat(opvVaccineCare.nextVaccineDose("0"), is(equalTo("OPV1")));
        assertThat(opvVaccineCare.nextVaccineDose("1"),is(equalTo("OPV2")));
        assertThat(opvVaccineCare.nextVaccineDose("2"),is(equalTo("OPV3")));
        assertThat(opvVaccineCare.nextVaccineDose("3"),is(equalTo(null)));
    }

    @Test
    public void shouldReturnANewEnrollmentForOPVCare() {
        final String facilityId = "facilityId";
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(5);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility(facilityId)));
        assertThat(new OPVVaccineCare(patient, enrollmentDate, null, null, null,CWC_OPV_OTHERS.getName()).newEnrollmentForCare(),
                is(equalTo(new PatientCare(CWC_OPV_OTHERS.getName(), birthDate, enrollmentDate, null, new HashMap<String, String>() {{
                    put("facilityId", facilityId);
                }}))));
    }

    @Test
    public void shouldReturnNullIfNotApplicableForOPVCare(){
        final String facilityId = "facilityId";
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(20);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility(facilityId)));
        assertNull(new OPVVaccineCare(patient, enrollmentDate, null, null, null,CWC_OPV_0.getName()).newEnrollmentForCare());
    }
}
