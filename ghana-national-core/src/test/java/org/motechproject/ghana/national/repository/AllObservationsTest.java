package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
    public void shouldVoidObservation() {
        MRSObservation mrsObservation = mock(MRSObservation.class);
        allObservations.voidObservation(mrsObservation, "reason", "123");
        verify(mockMrsObservationAdapter).voidObservation(mrsObservation, "reason", "123");
    }
}
