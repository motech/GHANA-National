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

public class PneumococcalVaccineCareTest {

    @Test
    public void shouldReturnNextDosageGivenADosage() {
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(9);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility("facilityId")));
        final PneumococcalVaccineCare pneumococcalVaccineCare = new PneumococcalVaccineCare(patient, null, null, null, null);
        assertThat(pneumococcalVaccineCare.nextVaccineDose("1"), is(equalTo("Pneumo2")));
        assertThat(pneumococcalVaccineCare.nextVaccineDose("2"), is(equalTo("Pneumo3")));
        assertThat(pneumococcalVaccineCare.nextVaccineDose("3"), is(equalTo(null)));
    }

    @Test
    public void shouldReturnANewEnrollmentForPneumococcalCare() {
        final String facilityId = "facilityId";
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(9);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility(facilityId)));
        assertThat(new PneumococcalVaccineCare(patient, enrollmentDate, null, null, null).newEnrollmentForCare(),
                is(equalTo(new PatientCare(ScheduleNames.CWC_PNEUMOCOCCAL.getName(), birthDate, enrollmentDate, null, new HashMap<String, String>() {{
                    put("facilityId", facilityId);
                }}))));
    }

    @Test
    public void shouldReturnNullIfNotApplicableForPneumococcalCare(){
        final String facilityId = "facilityId";
        LocalDate enrollmentDate = DateUtil.today();
        LocalDate birthDate=enrollmentDate.minusWeeks(20);
        Patient patient = new Patient(new MRSPatient("pid", "mid", new MRSPerson().dateOfBirth(birthDate.toDate()), new MRSFacility(facilityId)));
        assertNull(new PneumococcalVaccineCare(patient, enrollmentDate, null, null, null).newEnrollmentForCare());
    }
}
