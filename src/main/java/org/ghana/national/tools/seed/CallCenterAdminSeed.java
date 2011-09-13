package org.ghana.national.tools.seed;

import org.ghana.national.dao.AllCallCenterAdmins;
import org.ghana.national.domain.CallCenterAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallCenterAdminSeed extends Seed {
    @Autowired
    private AllCallCenterAdmins allCallCenterAdmins;

    @Override
    protected void load() {
        allCallCenterAdmins.add(new CallCenterAdmin("call", "call"));
    }
}
