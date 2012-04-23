package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
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

public class IPTiVaccineCareTest {

    @Test
    public void shouldReturnNextDosageGivenADosage() {
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(9);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility("facilityId")));
        final IPTiVaccineCare ipTiVaccineCare = new IPTiVaccineCare(patient, null, null, null, null);
        assertThat(ipTiVaccineCare.nextVaccineDose("1"), is(equalTo("IPTi2")));
        assertThat(ipTiVaccineCare.nextVaccineDose("2"), is(equalTo("IPTi3")));
        assertThat(ipTiVaccineCare.nextVaccineDose("3"), is(equalTo(null)));
    }

    @Test
    public void shouldReturnANewEnrollmentForIPTiCare() {
        final String facilityId = "facilityId";
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(9);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility(facilityId)));
        assertThat(new IPTiVaccineCare(patient, enrollmentDate, null, null, null).newEnrollmentForCare(),
                is(equalTo(new PatientCare(ScheduleNames.CWC_IPT_VACCINE, birthDate, enrollmentDate, null, new HashMap<String, String>() {{
                    put("facilityId", facilityId);
                }}))));
    }

    @Test
    public void shouldReturnNullIfNotApplicableForIPTiCare(){
        final String facilityId = "facilityId";
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(20);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility(facilityId)));
        assertNull(new IPTiVaccineCare(patient, enrollmentDate, null, null, null).newEnrollmentForCare());
    }
}
