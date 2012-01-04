package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        formErrors.addAll(formValidator.validatePatient(formBean.getPatientId(), PATIENT_ID_ATTRIBUTE_NAME));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        return formErrors;
    }
}
