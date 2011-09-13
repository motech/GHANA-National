package org.ghana.national.tools.seed;

import org.ghana.national.dao.AllSuperAdmins;
import org.ghana.national.domain.SuperAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminSeed extends Seed {
    @Autowired
    private AllSuperAdmins allSuperAdmins;

    @Override
    protected void load() {
        allSuperAdmins.add(new SuperAdmin("admin", "admin"));
    }
}
