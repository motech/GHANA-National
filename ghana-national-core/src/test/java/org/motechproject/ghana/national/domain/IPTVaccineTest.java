package org.motechproject.ghana.national.domain;

import org.junit.Test;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class IPTVaccineTest {

    @Test
    public void shouldCreateIPTVaccineFromANCVisit () {
        ANCVisitRequest ancVisit = new ANCVisitRequest().iptdose("NA");
        assertNull(IPTVaccine.createFromANCVisit(ancVisit));

        ANCVisitRequest ancVisitRequest = new ANCVisitRequest().iptdose("1").iptReactive(true).patient(mock(Patient.class));
        assertIPTVaccine(IPTVaccine.createFromANCVisit(ancVisitRequest), ancVisitRequest);
        ancVisitRequest = new ANCVisitRequest().iptdose("2").iptReactive(false).patient(mock(Patient.class));
        assertIPTVaccine(IPTVaccine.createFromANCVisit(ancVisitRequest), ancVisitRequest);
    }

    private void assertIPTVaccine(IPTVaccine iptVaccine, ANCVisitRequest request) {
        assertThat(iptVaccine.getGivenTo(), is(request.getPatient()));
        assertThat(iptVaccine.getIptDose(), is(IPTDose.byValue(request.getIptdose()).value()));
        assertThat(iptVaccine.getIptReaction(), is(IPTReaction.byValue(request.getIptReactive())));
    }
}
