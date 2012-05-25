package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.IS_NOT_ALIVE;
import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;

public class IsAlive extends PatientValidator{
    private FormError formError;

    public IsAlive() {
    }

    public IsAlive(FormError formError) {
        this.formError = formError;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmitted) {
        return patient.getMrsPatient().getPerson().isDead()? Arrays.asList(getFormError()): Collections.<FormError>emptyList();
    }

    private FormError getFormError() {
        return formError != null ? formError: new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE);
    }
}
