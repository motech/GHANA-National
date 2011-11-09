package org.motechproject.ghana.national.handlers;

import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GeneralQueryFormHandler implements FormPublishHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseQuery.GeneralQuery")
    public void handleFormEvent(MotechEvent event) {
        log.info(event.getSubject() + "|params=" + event.getParameters());
    }
}
