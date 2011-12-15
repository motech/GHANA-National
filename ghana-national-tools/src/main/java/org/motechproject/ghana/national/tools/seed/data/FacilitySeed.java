package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.FacilityNameAndId;
import org.motechproject.ghana.national.tools.seed.data.source.FacilitySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

@Component
public class FacilitySeed extends Seed {

    @Autowired
    AllFacilities allFacilities;

    @Autowired
    FacilitySource facilitySource;

    @Override
    protected void load() {
        try {
            List<FacilityNameAndId> nameAndOpenMrsIds = project(allFacilities.facilities(), FacilityNameAndId.class, on(Facility.class).getMrsFacility().getName(), on(Facility.class).getMrsFacility().getId());
            for (FacilityNameAndId nameAndMotechId : facilitySource.getMotechFacilityNameAndIds()) {
                String OpenMrsFacilityId = FacilityNameAndId.findByName(nameAndOpenMrsIds, nameAndMotechId.getName()).getId();
                allFacilities.saveLocally(new Facility().mrsFacilityId(OpenMrsFacilityId).motechId(nameAndMotechId.getId()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
