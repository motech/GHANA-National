package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientQueryFormValidator extends FormValidator<ClientQueryForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @LoginAsAdmin
    @ApiSession
    @Override
    public List<FormError> validate(ClientQueryForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validatePatient(formBean.getMotechId(), "motechId"));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        return formErrors;
    }
}
