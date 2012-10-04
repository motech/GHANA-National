package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

public class TTVaccineTest {

    @Test
    public void shouldCreateTTVaccineFromANCVisit(){
        assertNull(TTVaccine.createFromANCVisit(new ANCVisit()));

        final DateTime vaccineDate = DateUtil.newDateTime(2000, 1, 1,new Time(10,30));
        Patient patient = new Patient(new MRSPatient("mrsPatientId"));
        final TTVaccine ancVisit = TTVaccine.createFromANCVisit(new ANCVisit().date(vaccineDate.toDate()).ttdose("1").patient(patient));
        assertIfTTVaccineAreEqual(ancVisit, new TTVaccine(vaccineDate, TTVaccineDosage.TT1,patient));
    }

    public static void assertIfTTVaccineAreEqual(TTVaccine ttVaccine, TTVaccine ttVaccine1) {
        assertThat(ttVaccine.getDosage(), is(ttVaccine1.getDosage()));
        assertThat(ttVaccine.getVaccinationDate(), is(ttVaccine1.getVaccinationDate()));
        assertThat(ttVaccine.getPatient().getMRSPatientId(), is(ttVaccine1.getPatient().getMRSPatientId()));
    }

}
