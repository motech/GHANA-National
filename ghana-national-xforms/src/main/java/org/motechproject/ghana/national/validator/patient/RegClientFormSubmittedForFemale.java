package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.Constants.*;


public class RegClientFormSubmittedForFemale extends PatientValidator {
    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        final RegisterClientForm registerPatientForm = (RegisterClientForm) filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmittedWithinGroup).get(0);

        return PATIENT_GENDER_MALE.equals(registerPatientForm.getSex()) ?
                Arrays.asList(new FormError("Sex", GENDER_ERROR_MSG)) : Collections.<FormError>emptyList();
    }
}
