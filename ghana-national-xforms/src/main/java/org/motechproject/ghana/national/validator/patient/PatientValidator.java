package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

public abstract class PatientValidator {
    private PatientValidator onFailure;
    private PatientValidator onSuccess;

    public PatientValidator onFailure(PatientValidator validator){
        onFailure = validator;
        return this;
    }

    public PatientValidator onSuccess(PatientValidator validator){
        onSuccess = validator;
        return this;
    }

    public PatientValidator onSuccess(PatientValidator validator, boolean applyIf){
        if(applyIf)
            onSuccess = validator;
        return this;
    }

    public abstract List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms);

    public PatientValidator getOnFailure() {
        return onFailure;
    }

    public PatientValidator getOnSuccess() {
        return onSuccess;
    }

    public boolean hasOnSuccessValidation(){
        return onSuccess != null;
    }

    public boolean hasOnFailureValidation(){
        return onFailure != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientValidator)) return false;

        PatientValidator validator = (PatientValidator) o;

        if (onFailure != null ? !onFailure.equals(validator.onFailure) : validator.onFailure != null) return false;
        if (onSuccess != null ? !onSuccess.equals(validator.onSuccess) : validator.onSuccess != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = onFailure != null ? onFailure.hashCode() : 0;
        result = 31 * result + (onSuccess != null ? onSuccess.hashCode() : 0);
        return result;
    }
}
