package org.motechproject.ghana.national.mapper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT1;

public class TTVaccinationEnrollmentMapperTest extends BaseUnitTest{

    private DateTime now;

    @Before
    public void setUp() throws Exception {
        now = DateTime.now();
        mockCurrentDate(now);
    }

    @Test
    public void shouldMapTTVaccinationEnrollmentRequests(){
        final String patientId = "mrs patient id";

        final Patient patient = new Patient(new MRSPatient(patientId));
        final LocalDate vaccinationDate = DateUtil.newDate(2000, 1, 1);

        EnrollmentRequest request = new TTVaccinationEnrollmentMapper().map(patient, vaccinationDate, TT1.getScheduleMilestoneName());
        assertThat(request.getExternalId(), is(equalTo(patientId)));
        assertThat(request.getScheduleName(), is(equalTo(ScheduleNames.TT_VACCINATION_VISIT)));
        assertThat(request.getPreferredAlertTime(), is(equalTo(new Time(now.toLocalTime()))));
        assertThat(request.getReferenceDate(), is(equalTo(vaccinationDate)));
        assertThat(request.getStartingMilestoneName(), is(equalTo(TT1.getScheduleMilestoneName())));
        assertThat(request.enrollIntoMilestone(), is(true));
    }
}
