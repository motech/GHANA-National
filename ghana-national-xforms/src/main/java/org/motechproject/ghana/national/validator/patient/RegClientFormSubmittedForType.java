package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.is;

public class RegClientFormSubmittedForType extends PatientValidator{
    private PatientType patientType;

    public RegClientFormSubmittedForType(PatientType patientType) {
        this.patientType = patientType;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        final RegisterClientForm registerPatientForm = (RegisterClientForm) filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmittedWithinGroup).get(0);
        return patientType.equals(registerPatientForm.getRegistrantType()) ?
                 Collections.<FormError>emptyList() : Arrays.asList(new FormError("patient type","cannot be " + registerPatientForm.getRegistrantType()));
    }
}
