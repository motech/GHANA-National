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
import java.util.Collections;
import java.util.List;

@Component
public class RegisterCWCFormValidator extends  FormValidator<RegisterCWCForm> {

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
        List<FormError> patientErrors = formValidator.validatePatient(formBean.getMotechId(), MOTECH_ID_ATTRIBUTE_NAME);
        if (!patientErrors.isEmpty()) {
            formErrors.addAll(patientErrors);
        } else {
            formErrors.addAll(validateIfPatientIsAChild(formBean.getMotechId()));
        }
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        return formErrors;
    }

  protected  List<FormError> validateIfPatientIsAChild(String motechId) {
        if(motechId!=null && patientService.getAgeOfPatientByMotechId(motechId) >= 5){
            return new ArrayList<FormError>() {{
                add(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_ERR_MSG));
            }};
        }
        return Collections.emptyList();
    }


}
