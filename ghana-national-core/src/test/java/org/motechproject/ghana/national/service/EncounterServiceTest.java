package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mrs.model.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class EncounterServiceTest{

    private EncounterService encounterService;

    @Mock
    StaffService mockStaffService;

    @Mock
    AllEncounters mockAllEncounters;

    @Before
    public void setUp(){
        initMocks(this);
        encounterService = new EncounterService();
         ReflectionTestUtils.setField(encounterService, "staffService", mockStaffService);
         ReflectionTestUtils.setField(encounterService, "allEncounters", mockAllEncounters);
    }

    @Test
    public void shouldPersistEncounter(){
        String staffId="12345";
        String facilityId="12312";
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        MRSUser mrsUser = new MRSUser().id(staffId).person(new MRSPerson().id("341"));
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mrsUser);
        MRSPatient mrsPatient = new MRSPatient("45454");

        MRSEncounter mrsEncounter = encounterService.persistEncounter(mrsPatient, staffId, facilityId, Constants.ENCOUNTER_PATIENTREGVISIT, new Date(2011, 9, 1), mrsObservations);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(mrsEncounterArgumentCaptor.capture());
        MRSEncounter mrsEncounterSaved = mrsEncounterArgumentCaptor.getValue();
        assertEquals(mrsEncounterSaved.getCreator().getId(),staffId);
        assertEquals(mrsEncounterSaved.getFacility().getId(),facilityId);
        assertEquals(mrsEncounterSaved.getEncounterType(),Constants.ENCOUNTER_PATIENTREGVISIT);
    }
    
}
