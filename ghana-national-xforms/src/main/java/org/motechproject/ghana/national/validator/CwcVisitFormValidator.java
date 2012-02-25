package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.CWCVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ghana.national.domain.EncounterType.CWC_REG_VISIT;

@Component
public class CwcVisitFormValidator extends FormValidator<CWCVisitForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private AllEncounters allEncounters;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(CWCVisitForm formBean) {
        List<FormError> errors = new ArrayList<FormError>();
        errors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        errors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        errors.addAll(formValidator.validateIfPatientIsAliveAndIsAChild(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME));
        errors.addAll(validateIfPatientAlreadyEnrolledForCWC(formBean.getMotechId(), CWC_REG_VISIT.value()));
        return errors;
    }

    private ArrayList<FormError> validateIfPatientAlreadyEnrolledForCWC(final String motechId, String encounterCwcregvisit) {
        if (allEncounters.getLatest(motechId, encounterCwcregvisit) == null) {
            return new ArrayList<FormError>() {{
                add(new FormError(motechId, "NOT REGISTERED FOR CWC"));
            }};
        }
        return new ArrayList<FormError>();
    }
}
