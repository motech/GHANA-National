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

public class RegCWCFormSubmittedInSameUpload extends PatientValidator{
    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        final List<FormBean> registerPatientForms = filter(having(on(FormBean.class).getFormname(), is("registerCWC")), formsSubmittedWithinGroup);
        return registerPatientForms.size() == 1 ? Collections.<FormError>emptyList(): Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for CWC"));
    }
}
