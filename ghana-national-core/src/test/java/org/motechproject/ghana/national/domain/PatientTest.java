package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.DELIVERY;
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
        assertPatientCare(patientCares.get(0), DELIVERY, pregnancy.dateOfConception());
    }

    @Test
    public void shouldReturnPatientCareForIPTpForIfCurrentPregnancyWeekIsOnOrBeforeWeek13() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(28).plusDays(6).toLocalDate());
        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery());

        assertPatientCare(patientCares.get(1), ANC_IPT_VACCINE, pregnancy.dateOfConception());
    }

    @Test
    public void shouldNotReturnPatientCareForIPTpForIfCurrentPregnancyWeekIsAfterWeek13() {

        Pregnancy pregnancy = basedOnDeliveryDate(todayAs6June2012.plusWeeks(12).plusDays(6).toLocalDate());
        List<PatientCare> patientCares = new Patient().ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery());

        assertEquals(1, patientCares.size());
        assertPatientCare(patientCares.get(0), DELIVERY, pregnancy.dateOfConception());
    }

    private void assertPatientCare(PatientCare patientCare, String name, LocalDate startingOn) {
        assertThat(patientCare.name(), is(name));
        assertThat(patientCare.startingOn(), is(startingOn));
    }
}
