package org.motechproject.ghana.national.service;

import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.stereotype.Service;

@Service
public class ExecuteAsOpenMRSAdmin {

    @ApiSession
    @LoginAsAdmin
    public <R> R execute(OpenMRSServiceCall openMRSServiceCall){
        return (R) openMRSServiceCall.invoke();
    }

    public static interface OpenMRSServiceCall {
        Object invoke();
    }

}
