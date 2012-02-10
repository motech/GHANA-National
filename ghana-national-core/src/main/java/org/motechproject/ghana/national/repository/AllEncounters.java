package org.motechproject.ghana.national.repository;

import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class AllEncounters {

    @Autowired
    MRSEncounterAdapter mrsEncounterAdapter;

    public MRSEncounter save(MRSEncounter mrsEncounter) {
        return mrsEncounterAdapter.createEncounter(mrsEncounter);
    }
    
    public MRSEncounter fetchLatest(String motechId, String encounterType) {
        return mrsEncounterAdapter.getLatestEncounterByPatientMotechId(motechId, encounterType);
    }
}
