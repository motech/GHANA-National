package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PregnancyTerminationFormValidator extends FormValidator<PregnancyTerminationForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private AllEncounters allEncounters;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(PregnancyTerminationForm formBean, FormBeanGroup group) {

        List<FormError> formErrors = super.validate(formBean, group);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        final Patient patient = formValidator.getPatient(formBean.getMotechId());

        formErrors.addAll(new DependentValidator().validate(patient, group.getFormBeans(),
                new ExistsInDb().onSuccess(new IsAlive().onSuccess(new EnrolledToANC(allEncounters)))
                        .onFailure(new RegANCFormSubmittedInSameUpload())));

        return formErrors;
    }
}
