package org.motechproject.ghana.national.validator;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class RegisterClientFormValidator extends FormValidator {
    @Override
    public List<FormError> validate(FormBean formBean) {
        return super.validate(formBean);
    }
}
