package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
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
public class RegisterANCFormValidator extends FormValidator<RegisterANCForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MobileMidwifeValidator mobileMidwifeValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterANCForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(validatePatientAndStaff(formBean.getMotechId(), formBean.getStaffId()));
        formErrors.addAll(validateMobileMidwifeIfEnrolled(formBean));
        return formErrors;
    }

    public List<FormError> validatePatientAndStaff(String motechId, String staffId) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(validatePatient(motechId));
        formErrors.addAll(formValidator.validateIfStaffExists(staffId));
        return formErrors;
    }

    public List<FormError> validatePatient(String patientId) {
        final List<FormError> patientFormErrors = formValidator.validateIfPatientExistsAndIsAlive(patientId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        if (!patientFormErrors.isEmpty()) {
            patientFormErrors.addAll(patientFormErrors);
        } else {
            patientFormErrors.addAll(formValidator.validateIfPatientIsFemale(patientId, Constants.MOTECH_ID_ATTRIBUTE_NAME));
        }
        return patientFormErrors;
    }

    private List<FormError> validateMobileMidwifeIfEnrolled(RegisterANCForm formBean) {
        MobileMidwifeEnrollment midwifeEnrollment = formBean.createMobileMidwifeEnrollment();
        return midwifeEnrollment != null ? mobileMidwifeValidator.validateForIncludeForm(midwifeEnrollment) : Collections.<FormError>emptyList();
    }
}
