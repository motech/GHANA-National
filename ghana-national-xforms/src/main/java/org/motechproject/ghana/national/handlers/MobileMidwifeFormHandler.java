package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.RegisterClientAction;
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
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.mobileMidwife")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        try {
            final MobileMidwifeForm mobileMidwifeForm = (MobileMidwifeForm) event.getParameters().get(Constants.FORM_BEAN);
            if (RegisterClientAction.UN_REGISTER.equals(mobileMidwifeForm.getAction())) {
                mobileMidwifeService.unregister(mobileMidwifeService.findBy(mobileMidwifeForm.getPatientId()));
                return;
            }

            mobileMidwifeService.register(mobileMidwifeForm.createMobileMidwifeEnrollment());
        } catch (Exception e) {
            log.error("Exception occured in registering Mobile Midwife ", e);
        }
    }
}
