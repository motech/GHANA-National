package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.*;

import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class ExistsInDb extends PatientValidator{
    private FormError formError;
    private Patient patient;

    public ExistsInDb() {
    }

    public ExistsInDb(FormError formError) {
        this.formError = formError;
    }

    public ExistsInDb(Patient patient, FormError formError) {
        this(formError);
        this.patient = patient;
    }

    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms){
        patient = this.patient != null ? this.patient : patient;
        return (patient == null)? Arrays.asList(getFormError()):Collections.<FormError>emptyList();
    }

    private FormError getFormError() {
        return formError != null ? formError : new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND);
    }
}
