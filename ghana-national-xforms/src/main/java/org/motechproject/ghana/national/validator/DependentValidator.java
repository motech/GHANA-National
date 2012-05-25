package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

public class DependentValidator {

    public List<FormError> validate(Patient patient, List<FormBean> formBeans, PatientValidator validator){

        final List<FormError> formErrors = validator.validate(patient, formBeans);
        if(formErrors.isEmpty()){
            if(validator.hasOnSuccessValidation()){
                return validate(patient, formBeans, validator.getOnSuccess());
            }else {
                return formErrors;
            }
        }else{
            if(validator.hasOnFailureValidation()){
                return validate(patient, formBeans, validator.getOnFailure());
            }else {
                return formErrors;
            }
        }
    }
}
