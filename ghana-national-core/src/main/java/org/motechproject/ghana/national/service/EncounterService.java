package org.motechproject.ghana.national.service;

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

    public MRSEncounter persistEncounter(MRSPatient mrsPatient, String staffId, String facilityId, String encounterType,
                                         Date registrationDate, Set<MRSObservation> mrsObservations) {
        MRSUser user = staffService.getUserByEmailIdOrMotechId(staffId);
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        String patientId = mrsPatient.getId();
        MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                registrationDate, patientId, mrsObservations, encounterType);
        return allEncounters.save(mrsEncounter);
    }

    public MRSEncounter fetchLatestEncounter(String motechId,String encounterType){
         return allEncounters.fetchLatest(motechId,encounterType);
    }
}
