package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.TTVisitForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ttVisitFormValidator")
public class TTVisitFormValidator extends FormValidator<TTVisitForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(TTVisitForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        final Patient patient = formValidator.getPatient(formBean.getMotechId());
        final PatientValidator regClientFormValidations = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForPatientWithAgeLessThan(12));
        List<FormError> errors = new DependentValidator().validate(patient, group.getFormBeans(),
                allForms, new ExistsInDb().onSuccess(new IsAlive().onSuccess(new AgeMoreThan(12))).onFailure(regClientFormValidations));
        formErrors.addAll(errors);
        return formErrors;
    }
}
