package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class EncounterService {

    @Autowired
    StaffService staffService;

    @Autowired
    AllEncounters allEncounters;

    @Deprecated
    public MRSEncounter persistEncounter(MRSPatient mrsPatient, String staffId, String facilityId, String encounterType,
                                         Date dateOfEncounter, Set<MRSObservation> mrsObservations) {
        MRSUser user = staffService.getUserByEmailIdOrMotechId(staffId);
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        String patientId = mrsPatient.getId();
        MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                dateOfEncounter, patientId, mrsObservations, encounterType);
        return allEncounters.save(mrsEncounter);
    }

    public MRSEncounter persistEncounter(Encounter encounter) {
        MRSUser staff = encounter.getStaff();
        MRSEncounter mrsEncounter = new MRSEncounter(null, staff.getPerson(), staff, encounter.getFacility(),
                encounter.getDate(), encounter.getMrsPatient(), encounter.getObservations(), encounter.getType());
        return allEncounters.save(mrsEncounter);
    }

    public MRSEncounter fetchLatestEncounter(String motechId,String encounterType){
         return allEncounters.fetchLatest(motechId,encounterType);
    }
}
