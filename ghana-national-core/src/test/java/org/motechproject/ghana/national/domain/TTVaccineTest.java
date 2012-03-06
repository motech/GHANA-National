package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class TTVaccineTest {

    @Test
    public void shouldCreateTTVaccineFromANCVisit(){
        assertThat(TTVaccine.createFromANCVisit(new ANCVisitRequest()), is(equalTo(null)));

        final DateTime vaccineDate = DateUtil.newDateTime(2000, 1, 1,new Time(10,30));
        Patient patient = mock(Patient.class);
        final TTVaccine ancVisit = TTVaccine.createFromANCVisit(new ANCVisitRequest().date(vaccineDate.toDate()).ttdose("1").patient(patient));
        assertIfTTVaccineAreEqual(ancVisit, new TTVaccine(vaccineDate, TTVaccineDosage.TT1,patient));
    }

    public static void assertIfTTVaccineAreEqual(TTVaccine ttVaccine, TTVaccine ttVaccine1) {
        assertThat(ttVaccine.getDosage(), is(equalTo(ttVaccine1.getDosage())));
        assertThat(ttVaccine.getVaccinationDate(), is(equalTo(ttVaccine1.getVaccinationDate())));
        assertThat(ttVaccine.getPatient().getMRSPatientId(), is(equalTo(ttVaccine1.getPatient().getMRSPatientId())));
    }

}
