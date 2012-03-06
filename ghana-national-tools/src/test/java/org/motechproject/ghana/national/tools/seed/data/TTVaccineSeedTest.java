package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.service.VisitService;
import org.motechproject.ghana.national.tools.seed.data.source.TTVaccineSource;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TTVaccineSeedTest {

    @Mock
    private TTVaccineSource ttVaccineSource;
    @Mock
    private VisitService visitService;

    private TTVaccineSeed ttVaccineSeed;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineSeed = new TTVaccineSeed(ttVaccineSource, null, visitService);
    }

    @Test
    public void shouldEnrollForAppropriateTTVaccinationMileStoneWithReferenceDateCalculatedFromTheDueDate() {
        final DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, new Time(10, 10));
        final Patient patient = new Patient(new MRSPatient("1000"));
        ttVaccineSeed.enroll(referenceDate, "TT2", patient);
        ArgumentCaptor<TTVaccine> ttVaccineArgCaptor = ArgumentCaptor.forClass(TTVaccine.class);
        verify(visitService).createTTSchedule(ttVaccineArgCaptor.capture());
        assertIfTTVaccineAreEqual(ttVaccineArgCaptor.getValue(), new TTVaccine(referenceDate, TTVaccineDosage.TT2, patient));
    }

    public static void assertTTVaccines(List<TTVaccine> ttVaccines1, List<TTVaccine> ttVaccines2){
        for (int i = 0; i < ttVaccines1.size(); i++) {
            assertIfTTVaccineAreEqual(ttVaccines1.get(i), ttVaccines2.get(i));
        }
    }

    public static void assertIfTTVaccineAreEqual(TTVaccine ttVaccine, TTVaccine ttVaccine1) {
        assertThat(ttVaccine.getDosage(), is(equalTo(ttVaccine1.getDosage())));
        assertThat(ttVaccine.getVaccinationDate(), is(equalTo(ttVaccine1.getVaccinationDate())));
        assertThat(ttVaccine.getPatient().getMRSPatientId(), is(equalTo(ttVaccine1.getPatient().getMRSPatientId())));
    }

}
