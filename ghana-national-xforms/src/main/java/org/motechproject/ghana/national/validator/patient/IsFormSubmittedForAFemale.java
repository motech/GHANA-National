package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.*;

public class IsFormSubmittedForAFemale extends PatientValidator {
    private String gender;

    public IsFormSubmittedForAFemale(String gender) {
        this.gender = gender;
    }

    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        return PATIENT_GENDER_MALE.equals(gender) ?
                Arrays.asList(new FormError("Sex", GENDER_ERROR_MSG)) : Collections.<FormError>emptyList();
    }
}
