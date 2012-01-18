package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CareHistoryFormValidator extends FormValidator<CareHistoryForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    @Autowired
    private PatientService patientService;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(CareHistoryForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validatePatient(formBean.getMotechId(), "motechId"));
        return formErrors;
    }
}
