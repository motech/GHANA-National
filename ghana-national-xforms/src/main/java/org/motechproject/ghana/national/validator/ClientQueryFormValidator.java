package org.motechproject.ghana.national.validator;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;

import java.util.Collections;
import java.util.List;

public class ClientQueryFormValidator implements FormValidator {

    @Override
    public List<FormError> validate(FormBean formBean) {
        return Collections.EMPTY_LIST;
    }
}
