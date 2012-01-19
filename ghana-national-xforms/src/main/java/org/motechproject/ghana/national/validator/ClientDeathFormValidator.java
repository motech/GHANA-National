package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ClientDeathFormValidator extends FormValidator<ClientDeathForm> {

    @Autowired private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    public List<FormError> validate(ClientDeathForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validatePatient(formBean.getMotechId(), "motechId"));
        return formErrors;
    }
}
