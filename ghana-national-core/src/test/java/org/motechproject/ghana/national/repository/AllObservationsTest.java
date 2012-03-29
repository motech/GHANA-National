package org.motechproject.ghana.national.repository;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;

public class AllObservationsTest {

    @Mock
    MRSObservationAdapter mockMrsObservationAdapter;

    AllObservations allObservations;

    @Before
    public void setUp() {
        initMocks(this);
        allObservations = new AllObservations();
        ReflectionTestUtils.setField(allObservations, "mrsObservationAdapter", mockMrsObservationAdapter);
    }

    @Test
    public void shouldFindObservation() {
        allObservations.findObservation("11", "abc");
        verify(mockMrsObservationAdapter).findObservation("11", "abc");
    }

    @Test
    public void shouldUpdateEDDObservationsIfModified() {
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        String motechId = "12";
        String staffId = "staffId";
        Patient patient = new Patient(new MRSPatient(motechId, null, null));
        when(edd.getValue()).thenReturn(DateUtil.newDate(2010, 12, 11).toDate());
        when(mockMrsObservationAdapter.findObservation(motechId, PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockMrsObservationAdapter.findObservation(motechId, EDD.getName())).thenReturn(edd);

        Set<MRSObservation> eddObservations = allObservations.updateEDD(new Date(), patient, staffId);

        assertTrue(CollectionUtils.isNotEmpty(eddObservations));
        verify(mockMrsObservationAdapter).findObservation(motechId, PREGNANCY.getName());
        verify(mockMrsObservationAdapter).voidObservation(eq(edd), anyString(), eq(staffId));
    }

    @Test
    public void shouldCreateEDDObservationsIfNew() {
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        String motechId = "12";
        String staffId = "staffId";
        Patient patient = new Patient(new MRSPatient(motechId, null, null));
        when(edd.getValue()).thenReturn(DateUtil.newDate(2010, 12, 11).toDate());
        when(mockMrsObservationAdapter.findObservation(motechId, PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockMrsObservationAdapter.findObservation(motechId, EDD.getName())).thenReturn(null);

        Set<MRSObservation> eddObservations = allObservations.updateEDD(new Date(), patient, staffId);

        assertTrue(CollectionUtils.isNotEmpty(eddObservations));
        verify(mockMrsObservationAdapter).findObservation(motechId, PREGNANCY.getName());
        verify(mockMrsObservationAdapter, never()).voidObservation(Matchers.<MRSObservation>anyObject(), anyString(), eq(staffId));
    }

    @Test
    public void shouldNotCreateEDDObservationsDeliveryDateIsTheSame() {
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        String motechId = "12";
        String staffId = "staffId";
        Patient patient = new Patient(new MRSPatient(motechId, null, null));
        Date estimatedDeliveryDate = DateUtil.newDate(2010, 12, 11).toDate();
        when(edd.getValue()).thenReturn(estimatedDeliveryDate);
        when(mockMrsObservationAdapter.findObservation(motechId, PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockMrsObservationAdapter.findObservation(motechId, EDD.getName())).thenReturn(edd);

        Set<MRSObservation> eddObservations = allObservations.updateEDD(estimatedDeliveryDate, patient, staffId);

        assertTrue(CollectionUtils.isEmpty(eddObservations));
        verify(mockMrsObservationAdapter, never()).findObservation(motechId, PREGNANCY.getName());
        verify(mockMrsObservationAdapter, never()).voidObservation(eq(edd), anyString(), eq(staffId));
    }

    @Test
    public void shouldNotRescheduleEDDIfEddIsNotModified() {
        Set<MRSObservation> mrsObservations = allObservations.updateEDD(null, null, null);
        verifyZeroInteractions(mockMrsObservationAdapter);
        assertTrue(mrsObservations.isEmpty());
    }

    @Test
    public void shouldVoidObservations() {
        MRSObservation mockMRSObservation = mock(MRSObservation.class);
        String reason = "reason";
        String staffId = "staff-id";
        allObservations.voidObservation(mockMRSObservation, reason, staffId);

        verify(mockMrsObservationAdapter).voidObservation(mockMRSObservation, reason, staffId);
    }
}
