package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.helper.MobileRequestMapper;
import org.motechproject.ghana.national.service.PregnancyTerminationService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;

public class PregnancyTerminationFormHandler {

    @Autowired
    PregnancyTerminationService service;

    @MotechListener(subjects = {"form.validation.successful.NurseDataEntry.PregnancyTermination"})
    public void handleTermination(MotechEvent motechEvent) {
        PregnancyTerminationForm formBean = (PregnancyTerminationForm) motechEvent.getParameters().get(Constants.FORM_BEAN);
        service.terminatePregnancy(MobileRequestMapper.map(formBean));
    }
}
