package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.EditClientForm;
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

import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;


@Component
public class EditClientFormValidator extends FormValidator<EditClientForm> {
    @Autowired
    private PatientService patientService;
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;


    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(EditClientForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        if (formBean.getFacilityId() != null) {
            formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        }
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getUpdatePatientFacilityId()));
        formErrors.addAll(validateIfMotechId(formBean.getMotechId()));
        formErrors.addAll(validateIfMotherMotechId(formBean.getMotherMotechId()));
        return formErrors;
    }

    private List<FormError> validateIfMotechId(String motechId) {
        if (patientService.getPatientByMotechId(motechId) == null) {
            return new ArrayList<FormError>() {{
                add(new FormError("motechId", NOT_FOUND));
            }};
        }
        return Collections.emptyList();
    }

    private List<FormError> validateIfMotherMotechId(String motherMotechId) {
        if (motherMotechId != null && patientService.getPatientByMotechId(motherMotechId) == null) {
            return new ArrayList<FormError>() {{
                add(new FormError("motherMotechId", NOT_FOUND));
            }};
        }
        return Collections.emptyList();
    }
}
