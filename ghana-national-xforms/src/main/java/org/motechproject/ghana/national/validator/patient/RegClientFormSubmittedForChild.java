package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

public class RegClientFormSubmittedForChild extends PatientValidator {

    private FormError formError;

    public RegClientFormSubmittedForChild() {
    }

    public RegClientFormSubmittedForChild(FormError formError) {
        this.formError = formError;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        final RegisterClientForm registerPatientForm = (RegisterClientForm) filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmittedWithinGroup).get(0);
        return !PatientType.CHILD_UNDER_FIVE.equals(registerPatientForm.getRegistrantType()) ?
                Arrays.asList(getFormError()) : Collections.<FormError>emptyList();
    }

    private FormError getFormError() {
        return formError != null ? formError: new FormError("Client Type", "should be a child");
    }
}
