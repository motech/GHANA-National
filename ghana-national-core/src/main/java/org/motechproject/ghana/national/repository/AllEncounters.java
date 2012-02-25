package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Set;

@Repository
public class AllEncounters {

    @Autowired
    AllStaffs allStaffs;

    @Autowired
    MRSEncounterAdapter mrsEncounterAdapter;

    @Deprecated
    public MRSEncounter persistEncounter(MRSPatient mrsPatient, String staffId, String facilityId, String encounterType,
                                         Date dateOfEncounter, Set<MRSObservation> mrsObservations) {
        MRSUser user = allStaffs.getUserByEmailIdOrMotechId(staffId);
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        String patientId = mrsPatient.getId();
        MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                dateOfEncounter, patientId, mrsObservations, encounterType);
        return mrsEncounterAdapter.createEncounter(mrsEncounter);
    }

    public MRSEncounter persistEncounter(Encounter encounter) {
        MRSUser staff = encounter.getStaff();
        MRSEncounter mrsEncounter = new MRSEncounter(null, staff.getPerson(), staff, encounter.getFacility(),
                encounter.getDate(), encounter.getMrsPatient(), encounter.getObservations(), encounter.getType());
        return mrsEncounterAdapter.createEncounter(mrsEncounter);
    }

    public MRSEncounter getLatest(String motechId, String encounterType){
         return mrsEncounterAdapter.getLatestEncounterByPatientMotechId(motechId, encounterType);
    }
}
