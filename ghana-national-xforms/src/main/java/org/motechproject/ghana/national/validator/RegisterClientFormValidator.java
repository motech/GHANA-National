package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.service.PatientService;
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

import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

@Component
public class RegisterClientFormValidator extends FormValidator<RegisterClientForm> {

    @Autowired
    private PatientService patientService;
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterClientForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);
        Patient patient = formValidator.getPatient(formBean.getMotechId());
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));

        String mothersMotechId = "Mothers motech Id";

        PatientValidator validators = new AlwaysValid().onSuccess(new NotExistsInDb(), RegistrationType.USE_PREPRINTED_ID.equals(formBean.getRegistrationMode()))
                .onSuccess(new IsFormSubmittedForAChild(formBean.getDateOfBirth(),null), PatientType.CHILD_UNDER_FIVE.equals(formBean.getRegistrantType()))
                .onSuccess(new IsFormSubmittedForAFemale(formBean.getSex()), PatientType.PREGNANT_MOTHER.equals(formBean.getRegistrantType()))
                .onFailure(new ExistsInDb(new FormError(mothersMotechId, NOT_FOUND)));
        List<FormError> patientValidationErrors = getDependentValidator().validate(patient, group.getFormBeans(), allForms, validators);
        formErrors.addAll(patientValidationErrors);
        formErrors.addAll(formValidator.validateNHISExpiry(formBean.getNhisExpires()));
        if(formBean.getAddHistory())
            formErrors.addAll(historyDateValidator(formBean).validate(patient,group.getFormBeans(),allForms));
        return formErrors;
    }


    DependentValidator getDependentValidator() {
        return new DependentValidator();
    }

    HistoryDateValidator historyDateValidator(RegisterClientForm form) {
        return new HistoryDateValidator(form);
    }
}

