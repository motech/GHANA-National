package org.motechproject.ghana.national.repository;

import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.openmrs.services.OpenMRSEncounterAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class AllEncounters {

    @Autowired
    OpenMRSEncounterAdaptor openMRSEncounterAdaptor;
    
    public MRSEncounter save(MRSEncounter mrsEncounter) {
        return openMRSEncounterAdaptor.createEncounter(mrsEncounter);
    }
    
    public MRSEncounter fetchLatest(String motechId, String encounterType) {
        return openMRSEncounterAdaptor.getLatestEncounterByPatientMotechId(motechId, encounterType);
    }
}
