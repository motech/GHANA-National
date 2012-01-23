package org.motechproject.ghana.national.validator;

import org.aspectj.apache.bcel.classfile.ConstantString;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
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
public class RegisterANCFormValidator extends FormValidator<RegisterANCForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private PatientService patientService;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterANCForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(validatePatientAndStaff(formBean.getMotechId(), formBean.getStaffId()));
        return formErrors;
    }

    public List<FormError> validatePatientAndStaff(String motechId, String staffId) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(validatePatient(motechId));
        formErrors.addAll(formValidator.validateIfStaffExists(staffId));
        return formErrors;
    }

    public List<FormError> validatePatient(String patientId) {
        final List<FormError> patientFormErrors = formValidator.validatePatient(patientId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        if (!patientFormErrors.isEmpty()) {
            patientFormErrors.addAll(patientFormErrors);
        } else {
            patientFormErrors.addAll(validateGenderOfPatient(patientId));
        }
        return patientFormErrors;
    }

    List<FormError> validateGenderOfPatient(String motechId) {
        Patient patient = patientService.getPatientByMotechId(motechId);
        if (patient.getMrsPatient().getPerson().getGender().equals(Constants.PATIENT_GENDER_MALE)) {
            return new ArrayList<FormError>() {{
                add(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME, Constants.GENDER_ERROR_MSG));
            }};
        }
        return new ArrayList<FormError>();
    }
}
