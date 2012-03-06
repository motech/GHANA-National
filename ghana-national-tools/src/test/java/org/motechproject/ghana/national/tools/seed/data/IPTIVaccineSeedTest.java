package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.tools.seed.data.ScheduleMigrationSeedTest.assertTTEnrollmentRequest;

public class IPTIVaccineSeedTest {

    @Mock
    private AllSchedules allSchedules;
    private IPTIVaccineSeed iptiVaccineSeed;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        iptiVaccineSeed = new IPTIVaccineSeed(null, null, allSchedules);
    }

    @Test
    public void shouldEnrollIntoIPTIVaccineSchedule(){
        DateTime referenceDate = DateUtil.newDateTime(2012, 1, 1, new Time(10, 10));
        Patient patient = new Patient(new MRSPatient("10000"));

        iptiVaccineSeed.enroll(referenceDate, "IPTI1", patient);

        ArgumentCaptor<EnrollmentRequest> enrollmentCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules).enroll(enrollmentCaptor.capture());

        assertTTEnrollmentRequest(enrollmentCaptor.getValue(), referenceDate.toDateTime(), "IPT1", "10000", referenceDate.toDateTime());
    }
}
