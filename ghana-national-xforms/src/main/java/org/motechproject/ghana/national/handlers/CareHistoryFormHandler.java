package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.openmrs.services.OpenMRSConceptAdaptor;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CareHistoryFormHandler implements FormPublishHandler {

    public static final String FORM_BEAN = "formBean";
    private final Logger log = LoggerFactory.getLogger(CareHistoryFormHandler.class);
    @Autowired
    private OpenMRSConceptAdaptor openMrsConceptAdaptor;
    @Autowired
    private CareService careService;

    public static final String ENCOUNTER_PATIENTHISTORY = "PATIENTHISTORY";
    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.careHistory")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        CareHistoryForm form = (CareHistoryForm) event.getParameters().get(FORM_BEAN);


        
    }
}
