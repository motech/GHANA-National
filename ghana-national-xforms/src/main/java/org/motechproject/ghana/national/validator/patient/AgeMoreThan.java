package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AgeMoreThan extends PatientValidator {
    private int age;

    public AgeMoreThan(int age) {
        this.age = age;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmitted) {
        return patient.getAge() < age ? Arrays.asList(new FormError("Patient age", "is less than " + age)): Collections.<FormError>emptyList();
    }
}
