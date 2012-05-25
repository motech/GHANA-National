package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_MORE_ERR_MSG;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_PARAMETER;

public class IsAChild extends PatientValidator {
    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmitted) {
        return patient.getMrsPatient().getPerson().getAge() > 5 ?
                Arrays.asList(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)) : new ArrayList<FormError>();
    }
}
