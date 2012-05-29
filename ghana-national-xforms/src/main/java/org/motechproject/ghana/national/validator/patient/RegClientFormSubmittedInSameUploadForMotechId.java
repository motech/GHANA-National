package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.List;

public class RegClientFormSubmittedInSameUploadForMotechId extends PatientValidator {
    private String motechId;

    public RegClientFormSubmittedInSameUploadForMotechId(String motechId) {
        this.motechId = motechId;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        return new ArrayList<FormError>();
    }
}
