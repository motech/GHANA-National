package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.DeliveryForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.service.PregnancyService;
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
public class DeliveryFormHandler implements FormPublishHandler {

    Logger log = LoggerFactory.getLogger(DeliveryFormHandler.class);

    @Autowired
    PregnancyService pregnancyService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.delivery")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        try {
            DeliveryForm deliveryForm = (DeliveryForm) event.getParameters().get(Constants.FORM_BEAN);

            if(!deliveryForm.getMaternalDeath()) {
//                pregnancyService.;
            }

            //stop all pregnancy related schedules & mobile midwife.

            //create encounter and observations.

            //register child & include it in cwc program.

            //mark pregnancy & pregnancy_status observations as false.

            //handle death scenario

        } catch (Exception e) {
            log.error("Encountered error while saving care history details", e);
        }
    }

}
