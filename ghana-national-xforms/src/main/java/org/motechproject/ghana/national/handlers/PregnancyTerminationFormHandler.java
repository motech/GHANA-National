package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.helper.MobileRequestMapper;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PregnancyTerminationService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PregnancyTerminationFormHandler implements FormPublishHandler{

    @Autowired
    PregnancyTerminationService pregnancyTerminationService;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Override
    @MotechListener(subjects = {"form.validation.successful.NurseDataEntry.PregnancyTermination"})
    public void handleFormEvent(MotechEvent motechEvent) {
        PregnancyTerminationForm formBean = (PregnancyTerminationForm) motechEvent.getParameters().get(Constants.FORM_BEAN);
        pregnancyTerminationService.terminatePregnancy(MobileRequestMapper.map(formBean));
        mobileMidwifeService.unregister(formBean.getMotechId());

    }
}
