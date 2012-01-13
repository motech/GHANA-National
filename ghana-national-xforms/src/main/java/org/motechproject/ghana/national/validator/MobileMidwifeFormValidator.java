package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MobileMidwifeFormValidator extends FormValidator<MobileMidwifeForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    final static String PATIENT_ID_ATTRIBUTE_NAME = "patientId";

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(MobileMidwifeForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(validateFacilityPatientAndStaff(formBean.getPatientId(),formBean.getFacilityId(),formBean.getStaffId()));
        return formErrors;
    }

    public List<FormError> validateFacilityPatientAndStaff(String patientId, String facilityId, String staffId) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(formValidator.validatePatient(patientId, PATIENT_ID_ATTRIBUTE_NAME));
        formErrors.addAll(formValidator.validateIfFacilityExists(facilityId));
        formErrors.addAll(formValidator.validateIfStaffExists(staffId));
        return formErrors;
    }
}
