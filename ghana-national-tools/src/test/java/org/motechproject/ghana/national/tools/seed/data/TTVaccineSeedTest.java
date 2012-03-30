package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.tools.seed.data.ScheduleMigrationSeedTest.assertTTEnrollmentRequest;

public class TTVaccineSeedTest {

    @Mock
    private OldGhanaScheduleSource oldGhanaScheduleSource;

    private TTVaccineSeed ttVaccineSeed;
    @Mock
    private AllSchedules allSchedules;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineSeed = new TTVaccineSeed(oldGhanaScheduleSource, null, allSchedules);
    }

    @Test
    public void shouldEnrollForAppropriateTTVaccinationMileStoneWithReferenceDateCalculatedFromTheDueDate() {
        final DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, new Time(10, 10));
        final Patient patient = new Patient(new MRSPatient("1000", null, null, new MRSFacility("fid")));
        ttVaccineSeed.enroll(referenceDate, "TT2", patient);
        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules).enroll(enrollmentRequestCaptor.capture());
        assertTTEnrollmentRequest(enrollmentRequestCaptor.getValue(), referenceDate, "TT2", "1000", referenceDate, new HashMap<String, String>());
    }

    @Test
    public void shouldEnrollForTTVaccinationMileStoneWithAReferenceDateWhichIsOneWeekBeforeTheDueDate(){
        final DateTime dueDate = DateUtil.newDateTime(2012, 2, 8, new Time(10, 10));
        final Patient patient = new Patient(new MRSPatient("1000", null, null, new MRSFacility("fid")));
        ttVaccineSeed.enroll(dueDate, "TT1", patient);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules).enroll(enrollmentRequestCaptor.capture());
        DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, new Time(10, 10));
        assertTTEnrollmentRequest(enrollmentRequestCaptor.getValue(), referenceDate, "TT1", "1000", referenceDate, new HashMap<String, String>());
    }

}
