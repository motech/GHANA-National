package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class RegClientFormSubmittedInSameUpload extends PatientValidator{
    private FormError formError;

    public RegClientFormSubmittedInSameUpload() {
    }

    public RegClientFormSubmittedInSameUpload(FormError formError) {
        this.formError = formError;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmitted) {
        final List<FormBean> registerPatientForms = filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmitted);
        return registerPatientForms.size() == 1 ? Collections.<FormError>emptyList(): Arrays.asList(getFormError());
    }

    private FormError getFormError() {
        return formError!=null ? formError : new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND);
    }
}
