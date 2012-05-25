package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.domain.RegisterClientAction;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileMidwifeFormHandler{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MobileMidwifeForm mobileMidwifeForm) {
        try {
            if (RegisterClientAction.UN_REGISTER.equals(mobileMidwifeForm.getAction())) {
                mobileMidwifeService.unRegister(mobileMidwifeForm.getMotechId());
                return;
            }

            mobileMidwifeService.register(mobileMidwifeForm.createMobileMidwifeEnrollment());
        } catch (Exception e) {
            log.error("Exception occurred while processing Mobile Midwife form", e);
            throw new XFormHandlerException("Exception occurred while processing Mobile Midwife form", e);
        }
    }
}
