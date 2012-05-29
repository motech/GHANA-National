package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

public class DependentValidator {

    public List<FormError> validate(Patient patient, List<FormBean> formsWithinGroup, List<FormBean> allForms, PatientValidator validator){

        final List<FormError> formErrors = validator.validate(patient, formsWithinGroup, allForms);
        if(formErrors.isEmpty()){
            if(validator.hasOnSuccessValidation()){
                return validate(patient, formsWithinGroup, allForms, validator.getOnSuccess());
            }else {
                return formErrors;
            }
        }else{
            if(validator.hasOnFailureValidation()){
                return validate(patient, formsWithinGroup, allForms, validator.getOnFailure());
            }else {
                return formErrors;
            }
        }
    }
}
