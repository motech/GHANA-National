package org.motechproject.ghana.national.ivr.service;

import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientAuthenticationService {
    @Autowired
    private PatientService patientService;

    @LoginAsAdmin
    @ApiSession
    public boolean isRegistered(String motechId){
        return patientService.getPatientByMotechId(motechId) != null;
    }

}
