package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeliveryNotificationFormValidator extends FormValidator<DeliveryNotificationForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(DeliveryNotificationForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        final List<FormError> patientErrors = formValidator.validatePatient(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);
        formErrors.addAll(patientErrors);
        if (patientErrors.isEmpty()) {
            formErrors.addAll(formValidator.validateIfPatientIsFemale(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME));
            formErrors.addAll(formValidator.validateIfPatientIsNotAChild(formBean.getMotechId()));
        }
        return formErrors;
    }
}
