package org.motechproject.ghana.national.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT1;

public class TTVaccinationEnrollmentMapperTest extends BaseUnitTest {

    private DateTime now;

    @Before
    public void setUp() throws Exception {
        now = DateTime.now();
        mockCurrentDate(now);
    }

    @Test
    public void shouldMapTTVaccinationEnrollmentRequests() {
        final String patientId = "mrs patient id";

        final Patient patient = new Patient(new MRSPatient(patientId));
        final DateTime vaccinationDate = DateUtil.newDateTime(2000, 1, 1, new Time(10, 10));

        EnrollmentRequest request = new TTVaccinationEnrollmentMapper().map(new TTVaccine(vaccinationDate, TT1, patient));
        assertThat(request.getExternalId(), is(equalTo(patientId)));
        assertThat(request.getScheduleName(), is(equalTo(ScheduleNames.TT_VACCINATION_VISIT)));
        assertThat(request.getPreferredAlertTime(), is(equalTo(new Time(now.toLocalTime()))));
        assertThat(request.getReferenceDate(), is(equalTo(vaccinationDate.toLocalDate())));
        assertThat(request.getStartingMilestoneName(), is(equalTo(TT1.getScheduleMilestoneName())));
        assertThat(request.enrollIntoMilestone(), is(true));
    }
}
