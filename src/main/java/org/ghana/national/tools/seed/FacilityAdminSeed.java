package org.ghana.national.tools.seed;

import org.ghana.national.dao.AllFacilityAdmins;
import org.ghana.national.domain.FacilityAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacilityAdminSeed extends Seed {
    @Autowired
    private AllFacilityAdmins allFacilityAdmins;

    @Override
    protected void load() {
        allFacilityAdmins.add(new FacilityAdmin("facility", "facility"));
    }
}
