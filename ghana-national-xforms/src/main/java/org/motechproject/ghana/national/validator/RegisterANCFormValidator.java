package org.motechproject.ghana.national.validator;

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

    static final String MOTECH_ID_ATTRIBUTE_NAME = "motechId";
    static final String GENDER_ERROR_MSG = "should be female for registering into ANC";

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterANCForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(validatePatient(formBean.getMotechId(), formBean.getFacilityId(), formBean.getStaffId()));
        return formErrors;
    }

    public List<FormError> validatePatient(String patientId, String facilityId, String staffId) {
        final List<FormError> patientFormErrors = formValidator.validatePatient(patientId, MOTECH_ID_ATTRIBUTE_NAME);
        if (!patientFormErrors.isEmpty()) {
            patientFormErrors.addAll(patientFormErrors);
        } else {
            patientFormErrors.addAll(validateGenderOfPatient(patientId));
        }
        patientFormErrors.addAll(formValidator.validateIfFacilityExists(facilityId));
        patientFormErrors.addAll(formValidator.validateIfStaffExists(staffId));
        return patientFormErrors;
    }

    List<FormError> validateGenderOfPatient(String motechId) {
        Patient patient = patientService.getPatientByMotechId(motechId);
        if (patient.getMrsPatient().getPerson().getGender().equals(Constants.PATIENT_GENDER_MALE)) {
            return new ArrayList<FormError>() {{
                add(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG));
            }};
        }
        return new ArrayList<FormError>();
    }
}
