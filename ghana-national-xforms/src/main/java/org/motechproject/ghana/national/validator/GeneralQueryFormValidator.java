package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.GeneralQueryForm;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeneralQueryFormValidator extends FormValidator<GeneralQueryForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @LoginAsAdmin
    @ApiSession
    @Override
    public List<FormError> validate(GeneralQueryForm generalQueryForm, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(generalQueryForm, group, allForms);
        formErrors.addAll(formValidator.validateIfFacilityExists(generalQueryForm.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(generalQueryForm.getStaffId()));
        return formErrors;
    }
}
