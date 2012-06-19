package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.*;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileFormHandler implements FormPublishHandler {

    @Autowired
    private PatientRegistrationFormHandler patientRegistrationFormHandler;

    @Autowired
    private RegisterANCFormHandler registerANCFormHandler;

    @Autowired
    private ANCVisitFormHandler ancVisitFormHandler;

    @Autowired
    private CareHistoryFormHandler careHistoryFormHandler;

    @Autowired
    private ClientDeathFormHandler clientDeathFormHandler;

    @Autowired
    private ClientQueryFormHandler clientQueryFormHandler;

    @Autowired
    private CWCVisitFormHandler cwcVisitFormHandler;

    @Autowired
    private DeliveryFormHandler deliveryFormHandler;

    @Autowired
    private DeliveryNotificationFormHandler deliveryNotificationFormHandler;

    @Autowired
    private EditPatientFormHandler editPatientFormHandler;

    @Autowired
    private GeneralQueryFormHandler generalQueryFormHandler;

    @Autowired
    private MobileMidwifeFormHandler mobileMidwifeFormHandler;

    @Autowired
    private OutPatientVisitFormHandler outPatientVisitFormHandler;

    @Autowired
    private PNCBabyFormHandler pncBabyFormHandler;

    @Autowired
    private PNCMotherFormHandler pncMotherFormHandler;

    @Autowired
    private PregnancyTerminationFormHandler pregnancyTerminationFormHandler;

    @Autowired
    private RegisterCWCFormHandler registerCWCFormHandler;

    @Autowired
    private TTVisitFormHandler ttVisitFormHandler;

    @Override
    @MotechListener(subjects = "handle.valid.xforms.group")
    public void handleFormEvent(MotechEvent event) {
        FormBeanGroup formBeanGroup = (FormBeanGroup) event.getParameters().get("formBeanGroup");
        for (FormBean formBean : formBeanGroup.getFormBeans()) {
            if (formBean instanceof RegisterClientForm)
                patientRegistrationFormHandler.handleFormEvent((RegisterClientForm) formBean);
            else if (formBean instanceof RegisterANCForm)
                registerANCFormHandler.handleFormEvent((RegisterANCForm) formBean);
            else if (formBean instanceof ANCVisitForm)
                ancVisitFormHandler.handleFormEvent((ANCVisitForm) formBean);
            else if (formBean instanceof CareHistoryForm)
                careHistoryFormHandler.handleFormEvent((CareHistoryForm) formBean);
            else if (formBean instanceof ClientDeathForm)
                clientDeathFormHandler.handleFormEvent((ClientDeathForm) formBean);
            else if (formBean instanceof ClientQueryForm)
                clientQueryFormHandler.handleFormEvent((ClientQueryForm) formBean);
            else if (formBean instanceof CWCVisitForm)
                cwcVisitFormHandler.handleFormEvent((CWCVisitForm) formBean);
            else if (formBean instanceof DeliveryForm)
                deliveryFormHandler.handleFormEvent((DeliveryForm) formBean);
            else if (formBean instanceof DeliveryNotificationForm)
                deliveryNotificationFormHandler.handleFormEvent((DeliveryNotificationForm) formBean);
            else if (formBean instanceof EditClientForm)
                editPatientFormHandler.handleFormEvent((EditClientForm) formBean);
            else if (formBean instanceof GeneralQueryForm)
                generalQueryFormHandler.handleFormEvent((GeneralQueryForm) formBean);
            else if (formBean instanceof MobileMidwifeForm)
                mobileMidwifeFormHandler.handleFormEvent((MobileMidwifeForm) formBean);
            else if (formBean instanceof OutPatientVisitForm)
                outPatientVisitFormHandler.handleFormEvent((OutPatientVisitForm) formBean);
            else if (formBean instanceof PNCBabyForm)
                pncBabyFormHandler.handleFormEvent((PNCBabyForm) formBean);
            else if (formBean instanceof PNCMotherForm)
                pncMotherFormHandler.handleFormEvent((PNCMotherForm) formBean);
            else if (formBean instanceof PregnancyTerminationForm)
                pregnancyTerminationFormHandler.handleFormEvent((PregnancyTerminationForm) formBean);
            else if (formBean instanceof RegisterCWCForm)
                registerCWCFormHandler.handleFormEvent((RegisterCWCForm) formBean);
            else if (formBean instanceof TTVisitForm)
                ttVisitFormHandler.handleFormEvent((TTVisitForm) formBean);
        }
    }
}
