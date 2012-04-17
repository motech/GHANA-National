package org.motechproject.ghana.national.domain;

import org.junit.Test;
import org.motechproject.ghana.national.domain.care.IPTVaccineCare;
import org.motechproject.ghana.national.domain.care.TTVaccineCare;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class CareHistoryTest {
    @Test
    public void shouldCreateCareForHistory() {
        IPTVaccineCare mockIptVaccineCare = mock(IPTVaccineCare.class);
        TTVaccineCare mockTTVaccineCare = mock(TTVaccineCare.class);
        CareHistory careHistory = CareHistory.forPregnancy(mockTTVaccineCare, mockIptVaccineCare);

        PatientCare iptPatientCare = mock(PatientCare.class);
        PatientCare ttPatientCare = mock(PatientCare.class);
        when(mockIptVaccineCare.careForHistory()).thenReturn(iptPatientCare);
        when(mockTTVaccineCare.careForHistory()).thenReturn(ttPatientCare);

        List<PatientCare> patientCares = careHistory.cares();
        verify(mockIptVaccineCare).careForHistory();
        verify(mockTTVaccineCare).careForHistory();
        assertThat(patientCares, is(asList(ttPatientCare, iptPatientCare)));
    }

}
