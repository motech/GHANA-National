package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.source.TTVaccineSource;
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
    private TTVaccineSource ttVaccineSource;

    private TTVaccineSeed ttVaccineSeed;
    @Mock
    private AllSchedules allSchedules;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineSeed = new TTVaccineSeed(ttVaccineSource, null, allSchedules);
    }

    @Test
    public void shouldEnrollForAppropriateTTVaccinationMileStoneWithReferenceDateCalculatedFromTheDueDate() {
        final DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, new Time(10, 10));
        final Patient patient = new Patient(new MRSPatient("1000"));
        ttVaccineSeed.enroll(referenceDate, "TT2", patient);
        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules).enroll(enrollmentRequestCaptor.capture());
        assertTTEnrollmentRequest(enrollmentRequestCaptor.getValue(), referenceDate, "TT2", "1000");
    }

    public static void assertTTEnrollmentRequest(EnrollmentRequest enrollmentRequest, DateTime referenceDate, String milestoneName, String externalId) {
        assertThat(enrollmentRequest.getReferenceDate(), is(equalTo(referenceDate.toLocalDate())));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(milestoneName)));
        assertThat(enrollmentRequest.getExternalId(), is(equalTo(externalId)));
    }
}
