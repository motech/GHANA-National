package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutPatientVisitFormValidator extends FormValidator<OutPatientVisitForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;


    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(OutPatientVisitForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        if (Boolean.FALSE.equals(formBean.isVisitor())) {
            final List<FormError> patientErrors = formValidator.validateIfPatientExistsAndIsAlive(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);
            formErrors.addAll(patientErrors);
            if (patientErrors.isEmpty()) {
                if (PatientType.CHILD_UNDER_FIVE.equals(formBean.getRegistrantType())) {
                    formErrors.addAll(formValidator.validateIfPatientIsAChild(formBean.getMotechId()));
                }
                if (PatientType.PREGNANT_MOTHER.equals(formBean.getRegistrantType())) {
                    formErrors.addAll(formValidator.validateIfPatientIsFemale(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME));
                }
            }
        }
        return formErrors;
    }
}
