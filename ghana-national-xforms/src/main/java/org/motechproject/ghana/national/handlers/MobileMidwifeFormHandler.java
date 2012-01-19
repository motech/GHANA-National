package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileMidwifeFormHandler implements FormPublishHandler {
    public static final String FORM_BEAN = "formBean";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.mobileMidwife")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        final MobileMidwifeForm mobileMidwifeForm = (MobileMidwifeForm) event.getParameters().get(FORM_BEAN);
        mobileMidwifeService.createOrUpdateEnrollment(mobileMidwifeForm.createMobileMidwifeEnrollment());
    }
}
