package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.TTVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ttVisitFormValidator")
public class TTVisitFormValidator extends FormValidator<TTVisitForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    public List<FormError> validate(TTVisitForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(validatePatient(formBean.getMotechId()));
        return formErrors;
    }

    public List<FormError> validatePatient(String motechId) {
        List<FormError> patientErrors = formValidator.validateIfPatientExistsAndIsAlive(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        if(patientErrors.isEmpty()){
             if(!formValidator.isAgeGreaterThan(motechId,12)){
                 patientErrors.add(new FormError("Patient age","is less than 12"));
             }
        }
        return patientErrors;
    }
}
