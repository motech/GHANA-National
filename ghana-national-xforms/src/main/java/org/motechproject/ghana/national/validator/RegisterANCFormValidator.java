package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterANCFormValidator extends FormValidator<RegisterANCForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterANCForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formValidator.validatePatient(formErrors, formBean.getMotechId());
        formValidator.validateIfFacilityExists(formErrors, formBean.getFacilityId());
        formValidator.validateIfStaffExists(formErrors, formBean.getStaffId());
        return formErrors;
    }
}
