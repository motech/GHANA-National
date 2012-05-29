package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PncBabyFormValidator extends org.motechproject.mobileforms.api.validator.FormValidator<PNCBabyForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(PNCBabyForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        final Patient patient = formValidator.getPatient(formBean.getMotechId());
        formErrors.addAll(dependentValidator().validate(patient, group.getFormBeans(),
                allForms, new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsAChild()))
                        .onFailure(new RegClientFormSubmittedInSameUpload()
                                        .onSuccess(new RegClientFormSubmittedForChild(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.CHILD_AGE_MORE_ERR_MSG))))));
        return formErrors;
    }

    DependentValidator dependentValidator() {
        return new DependentValidator();
    }
}
