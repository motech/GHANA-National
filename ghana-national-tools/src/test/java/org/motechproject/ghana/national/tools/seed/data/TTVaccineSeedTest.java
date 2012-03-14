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
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
        final Patient patient = new Patient(new MRSPatient("1000"));
        ttVaccineSeed.enroll(referenceDate, "TT2", patient);
        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules).enroll(enrollmentRequestCaptor.capture());
        assertTTEnrollmentRequest(enrollmentRequestCaptor.getValue(), referenceDate, "TT2", "1000", referenceDate);
    }

    public static void assertTTEnrollmentRequest(EnrollmentRequest enrollmentRequest, DateTime referenceDateTime, String milestoneName, String externalId, DateTime enrollmentDateTime) {
        assertThat(enrollmentRequest.getReferenceDateTime(), is(equalTo(referenceDateTime)));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(milestoneName)));
        assertThat(enrollmentRequest.getExternalId(), is(equalTo(externalId)));
        assertThat(enrollmentRequest.getEnrollmentDateTime(), is(equalTo(enrollmentDateTime)));
    }
}
