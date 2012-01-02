package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MobileMidwifeFormValidator extends FormValidator<RegisterClientForm> {

    static final String IS_NOT_ALIVE = "is not alive";

    @Autowired
    PatientService patientService;

    @Override
    public List<FormError> validate(RegisterClientForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        validatePatient(formErrors, formBean);
        return formErrors;
    }

    private List<FormError> validatePatient(List<FormError> formErrors, RegisterClientForm formBean) {
        Patient patient = patientService.getPatientByMotechId(formBean.getMotechId());
        if(patient.getMrsPatient().getPerson().isDead()){
            formErrors.add(new FormError("motechId", MobileMidwifeFormValidator.IS_NOT_ALIVE));
        }
        return formErrors;
    }
}
