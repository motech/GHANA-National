package org.motechproject.ghana.national.repository;

import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.openmrs.services.OpenMRSEncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class AllEncounters {

    @Autowired
    OpenMRSEncounterAdapter openMRSEncounterAdapter;

    public MRSEncounter save(MRSEncounter mrsEncounter) {
        return openMRSEncounterAdapter.createEncounter(mrsEncounter);
    }
    
    public MRSEncounter fetchLatest(String motechId, String encounterType) {
        return openMRSEncounterAdapter.getLatestEncounterByPatientMotechId(motechId, encounterType);
    }
}
