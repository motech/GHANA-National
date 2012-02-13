package org.motechproject.ghana.national.handlers;

import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

@Component
public class TTVisitFormHandler implements FormPublishHandler {

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.TTVisit")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {

    }
}
