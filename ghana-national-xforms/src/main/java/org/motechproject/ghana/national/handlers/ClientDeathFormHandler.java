package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDeathFormHandler{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PatientService patientService;

    @Autowired
    private MobileMidwifeService mobileMidwifeService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(ClientDeathForm clientDeathForm) {
        try{

            patientService.deceasePatient(clientDeathForm.getDate(), clientDeathForm.getMotechId(), clientDeathForm.getCauseOfDeath(), clientDeathForm.getComment());
            mobileMidwifeService.unRegister(clientDeathForm.getMotechId());
        }
        catch (Exception e) {
            log.error("Encountered error while processing client death form", e);
            throw new XFormHandlerException("Encountered error while processing client death form", e);
        }

    }
}
