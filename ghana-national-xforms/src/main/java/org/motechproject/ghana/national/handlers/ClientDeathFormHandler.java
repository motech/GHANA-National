package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDeathFormHandler implements FormPublishHandler {
    public static final String FORM_BEAN = "formBean";
    @Autowired private PatientService patientService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.clientDeath")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        ClientDeathForm clientDeathForm = (ClientDeathForm) event.getParameters().get(FORM_BEAN);
        patientService.deceasePatient(clientDeathForm.getMotechId(), clientDeathForm.getDate(), clientDeathForm.getCauseOfDeath(), clientDeathForm.getComment());
    }
}
