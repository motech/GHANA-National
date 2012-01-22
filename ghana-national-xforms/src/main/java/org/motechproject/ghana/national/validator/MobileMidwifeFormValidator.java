package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MobileMidwifeFormValidator extends FormValidator<MobileMidwifeForm> {

    @Autowired
    private MobileMidwifeValidator mobileMidwifeValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(MobileMidwifeForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(mobileMidwifeValidator.validate(formBean.createMobileMidwifeEnrollment()));
        return formErrors;
    }
}
