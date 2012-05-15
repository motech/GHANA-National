package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;

public class TTVaccineCareTest {
    @Test
    public void shouldReturnNextDosageGivenADosage() {
        final TTVaccineCare ttVaccineCare = new TTVaccineCare(null, null, null, null, null, null);
        assertThat(ttVaccineCare.nextVaccineDose("1"), is(equalTo("TT2")));
        assertThat(ttVaccineCare.nextVaccineDose("5"), is(equalTo(null)));
    }

    @Test
    public void shouldReturnANewEnrollmentForTTCare() {
        final String facilityId = "facilityId";
        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        LocalDate enrollmentDate = DateUtil.today();
        assertThat(new TTVaccineCare(patient, null, enrollmentDate, null, null, null).newEnrollmentForCare(),
                is(equalTo(new PatientCare(TT_VACCINATION.getName(), enrollmentDate, null, null, new HashMap<String, String>() {{
                    put("facilityId", facilityId);
                }}))));
    }
}
