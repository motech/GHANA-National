package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class IPTVaccineTest {

    @Test
    public void shouldCreateIPTVaccineFromANCVisit () {
        ANCVisit ancVisit = new ANCVisit().iptdose("NA");
        assertNull(IPTVaccine.createFromANCVisit(ancVisit));

        ANCVisit ancVisitRequest = new ANCVisit().iptdose("1").iptReactive(true).patient(mock(Patient.class));
        assertIPTVaccine(IPTVaccine.createFromANCVisit(ancVisitRequest), ancVisitRequest);
        ancVisitRequest = new ANCVisit().iptdose("2").iptReactive(false).patient(mock(Patient.class));
        assertIPTVaccine(IPTVaccine.createFromANCVisit(ancVisitRequest), ancVisitRequest);
    }

    @Test
    public void shouldCreateIPTVaccineFromCWCVisit () {
        CWCVisit cwcVisit = new CWCVisit().iptidose("NA");
        assertNull(IPTVaccine.createFromCWCVisit(cwcVisit));

        cwcVisit = new CWCVisit().iptidose("1").patient(mock(Patient.class));
        assertIPTVaccine(IPTVaccine.createFromCWCVisit(cwcVisit), cwcVisit);
        cwcVisit = new CWCVisit().iptidose("2").patient(mock(Patient.class));
        assertIPTVaccine(IPTVaccine.createFromCWCVisit(cwcVisit), cwcVisit);
    }

    private void assertIPTVaccine(IPTVaccine iptVaccine, CWCVisit cwcVisit) {
        assertThat(iptVaccine.getGivenTo(), is(cwcVisit.getPatient()));
        assertThat(iptVaccine.getIptDose(), is(IPTDose.byValue(cwcVisit.getIptidose()).value()));
    }

    private void assertIPTVaccine(IPTVaccine iptVaccine, ANCVisit request) {
        assertThat(iptVaccine.getGivenTo(), is(request.getPatient()));
        assertThat(iptVaccine.getIptDose(), is(IPTDose.byValue(request.getIptdose()).value()));
        assertThat(iptVaccine.getIptReaction(), is(IPTReaction.byValue(request.getIptReactive())));
    }
}
