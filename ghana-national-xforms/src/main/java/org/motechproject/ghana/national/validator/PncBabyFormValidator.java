package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PncBabyFormValidator extends org.motechproject.mobileforms.api.validator.FormValidator<PNCBabyForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(PNCBabyForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfPatientExistsAndIsAlive(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME)) ;
        formErrors.addAll(formValidator.validateIfPatientIsAChild(formBean.getMotechId())) ;
        return  formErrors;
    }
}
