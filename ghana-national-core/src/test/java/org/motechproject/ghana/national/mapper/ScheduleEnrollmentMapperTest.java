package org.motechproject.ghana.national.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ScheduleEnrollmentMapperTest {

    @Test
    public void shouldMapEnrollmentRequests() {
        final String patientId = "mrs patient id";
        final Patient patient = new Patient(new MRSPatient(patientId));
        Time referenceTime = new Time(10, 10);
        final DateTime referenceDate = DateUtil.newDateTime(2000, 1, 1, referenceTime);
        final DateTime registeredDate = DateUtil.newDateTime(2000, 2, 3, referenceTime);

        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put("data", "value");
        String scheduleName = "name";
        PatientCare patientCare = new PatientCare(scheduleName, referenceDate, registeredDate, metaData);
        String startMilestone = "startMilestone";
        EnrollmentRequest request = new ScheduleEnrollmentMapper().map(patient, patientCare, startMilestone);

        assertThat(request.getExternalId(), is(patientId));
        assertThat(request.getScheduleName(), is(scheduleName));
        assertThat(request.getPreferredAlertTime(), is(equalTo(null)));
        assertThat(request.getReferenceDateTime(), is(referenceDate));
        assertThat(request.getEnrollmentDateTime(), is(registeredDate));
        assertThat(request.getStartingMilestoneName(), is(startMilestone));
        assertThat(request.getMetadata(), is(metaData));
    }
}
