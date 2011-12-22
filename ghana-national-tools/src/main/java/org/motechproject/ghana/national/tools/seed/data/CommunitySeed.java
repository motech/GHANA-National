package org.motechproject.ghana.national.tools.seed.data;


import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.AllCommunities;
import org.motechproject.ghana.national.tools.seed.data.source.Community;
import org.motechproject.ghana.national.tools.seed.data.source.CommunitySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommunitySeed extends Seed {

    @Autowired
    CommunitySource communitySource;
    @Autowired
    AllCommunities allCommunities;

    @Override
    protected void load() {
        try {
            List<Community> communities = communitySource.getAllCommunities();
            for (Community community : communities) {
                allCommunities.add(community);
            }

           /* To be used if we need to migrate the community_id --> placing the community_id property in Patient model
            Map<String, String> patientIdsToCommunity = communitySource.patientsAssociatedToCommunity();
            List<Patient> patients = allPatients.findByPatientIds(patientIdsToCommunity.keySet());
            updatePatientWithCommunityId(patients, patientIdsToCommunity);*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* To be used if we need to migrate the community_id --> placing the community_id property in Patient model
    private void updatePatientWithCommunityId(List<Patient> patients, Map<String, String> patientIdsToCommunity) {

        for(Patient patient : patients ) {
            patient.communityId(patientIdsToCommunity.get(patient.getId()));
            allPatients.update(patient);
        }
    }*/
}
