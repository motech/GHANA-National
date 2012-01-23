package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

@Component
public class ClientDeathFormValidator extends FormValidator<ClientDeathForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    @Autowired
    private PatientService patientService;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(ClientDeathForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        final Patient patient = patientService.getPatientByMotechId(formBean.getMotechId());
        if (patient == null) {
            formErrors.add(new FormError("motechId", NOT_FOUND));
        }
        return formErrors;
    }
}
