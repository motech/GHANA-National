package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.util.DateUtil.today;

public class IPTVaccineCareTest {
    @Test
    public void shouldReturnNextDosageGivenADosage() {
        final IPTVaccineCare iptVaccineCare = new IPTVaccineCare(null, null, null, null, null);
        assertThat(iptVaccineCare.nextVaccineDose("1"), is(equalTo("IPT2")));
        assertThat(iptVaccineCare.nextVaccineDose("3"), is(equalTo(null)));
    }

    @Test
    public void shouldReturnANewEnrollmentForIPTCareIfPregnancyIsApplicableForIPT(){

        final String facilityId = "facilityId";

        final LocalDate conceptionDate = today().minusWeeks(14);
        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate);

        assertThat(new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), null, null, null).newEnrollmentForCare(), is(equalTo(new PatientCare(ANC_IPT_VACCINE.getName(), conceptionDate, null, null, new HashMap<String, String>(){{
            put("facilityId", facilityId);
        }}))));

        pregnancy = Pregnancy.basedOnConceptionDate(today().plusWeeks(70));
        assertThat(new IPTVaccineCare(patient, pregnancy.dateOfDelivery(), null, null, null).newEnrollmentForCare(), is(equalTo(null)));

    }
}
