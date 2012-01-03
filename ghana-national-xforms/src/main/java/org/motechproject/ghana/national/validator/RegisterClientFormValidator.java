package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

@Component
public class RegisterClientFormValidator extends FormValidator<RegisterClientForm> {

    @Autowired
    private StaffService staffService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private PatientService patientService;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterClientForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        validateIfStaffExists(formErrors, formBean.getStaffId());
        validateIfFacilityExists(formErrors, formBean.getFacilityId());
        validateIfMotechId(formErrors, formBean.getMotechId(), formBean.getRegistrationMode());
        validateIfMotherMotechId(formErrors, formBean.getMotherMotechId(), formBean.getRegistrantType());
        return formErrors;
    }

    private void validateIfMotechId(List<FormError> formErrors, String motechId, RegistrationType registrationType) {
        if (RegistrationType.USE_PREPRINTED_ID.equals(registrationType) && patientService.getPatientByMotechId(motechId) != null)
            formErrors.add(new FormError("motechId", "in use"));
    }

    private void validateIfMotherMotechId(List<FormError> formErrors, String motherMotechId, PatientType patientType) {
        if (PatientType.CHILD_UNDER_FIVE.equals(patientType)) {
            if (motherMotechId == null || patientService.getPatientByMotechId(motherMotechId) == null) {
                formErrors.add(new FormError("motherMotechId", NOT_FOUND));
            }
        }
    }

    private void validateIfStaffExists(List<FormError> formErrors, final String staffId) {
        if (staffService.getUserById(staffId) == null)
            formErrors.add(new FormError("staffId", NOT_FOUND));
    }

    private void validateIfFacilityExists(List<FormError> formErrors, String facilityId) {
        if (facilityService.getFacilityByMotechId(facilityId) == null)
            formErrors.add(new FormError("facilityId", NOT_FOUND));
    }


}
