package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.*;

import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class ExistsInDb extends PatientValidator{
    private FormError formError;

    public ExistsInDb() {
    }

    public ExistsInDb(FormError formError) {
        this.formError = formError;
    }

    public List<FormError> validate(Patient patient, List<FormBean> formsSubmitted){
        return (patient == null)? Arrays.asList(getFormError()):Collections.<FormError>emptyList();
    }

    private FormError getFormError() {
        return formError != null ? formError : new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND);
    }
}
