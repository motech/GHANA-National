package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegisterCWCFormValidator extends FormValidator<RegisterCWCForm> {

    public static final String MOTECH_ID_ATTRIBUTE_NAME = "motechId";
    public static final String CHILD_AGE_PARAMETER = "childAge";
    public static final String CHILD_AGE_ERR_MSG = "child cannot be more than 5 years old";

    @Autowired
    private PatientService patientService;


    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterCWCForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(validatePatient(formBean.getMotechId()));
        return formErrors;
    }

    public List<FormError> validatePatient(String motechId) {
        List<FormError> patientErrors = formValidator.validatePatient(motechId, MOTECH_ID_ATTRIBUTE_NAME);
        return !patientErrors.isEmpty() ? patientErrors : validateIfPatientIsAChild(motechId);
    }

    private List<FormError> validateIfPatientIsAChild(String motechId) {
        if (motechId != null && patientService.getAgeOfPatientByMotechId(motechId) >= 5) {
            return new ArrayList<FormError>() {{
                add(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_ERR_MSG));
            }};
        }
        return new ArrayList<FormError>();
    }
}
