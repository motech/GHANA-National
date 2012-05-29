package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.EditClientForm;
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

import static org.motechproject.ghana.national.domain.Constants.IS_NOT_ALIVE;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;


@Component
public class EditClientFormValidator extends FormValidator<EditClientForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;


    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(EditClientForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);

        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));

        if (formBean.getUpdatePatientFacilityId() != null) {
            formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getUpdatePatientFacilityId()));
        }

        List<FormBean> formsSubmitted = group.getFormBeans();
        PatientValidator patientValidator = new ExistsInDb().onSuccess(new IsAlive()).onFailure(new RegClientFormSubmittedInSameUpload());
        formErrors.addAll(dependentValidator().validate(formValidator.getPatient(formBean.getMotechId()), formsSubmitted, allForms, patientValidator));
        if (formBean.getMotherMotechId() != null)
            validateMother(formBean, formErrors, formsSubmitted, allForms);

        return formErrors;
    }

    private void validateMother(EditClientForm formBean, List<FormError> formErrors, List<FormBean> formsWithinGroup, List<FormBean> allForms) {

        String mothersMotechId = "Mothers motech Id";
        PatientValidator motherValidator = new ExistsInDb(new FormError(mothersMotechId, NOT_FOUND))
                .onSuccess(new IsAlive(new FormError(mothersMotechId, IS_NOT_ALIVE)));
        formErrors.addAll(dependentValidator().validate(formValidator.getPatient(formBean.getMotherMotechId()), formsWithinGroup, allForms, motherValidator));
    }

    public DependentValidator dependentValidator() {
        return new DependentValidator();
    }
}
