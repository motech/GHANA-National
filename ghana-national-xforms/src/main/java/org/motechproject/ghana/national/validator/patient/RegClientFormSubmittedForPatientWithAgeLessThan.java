package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

public class RegClientFormSubmittedForPatientWithAgeLessThan extends PatientValidator {
    private int age;

    public RegClientFormSubmittedForPatientWithAgeLessThan(int age) {
        this.age = age;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmitted) {
        final RegisterClientForm registerPatientForm = (RegisterClientForm) filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmitted).get(0);
        return DateUtil.getDifferenceOfDatesInYears(registerPatientForm.getDateOfBirth()) < age ?
                Arrays.asList(new FormError("Patient age", "is less than " + age)): Collections.<FormError>emptyList();
    }
}
