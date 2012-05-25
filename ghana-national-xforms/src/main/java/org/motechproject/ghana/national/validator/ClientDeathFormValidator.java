package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.validator.patient.ExistsInDb;
import org.motechproject.ghana.national.validator.patient.RegClientFormSubmittedInSameUpload;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientDeathFormValidator extends FormValidator<ClientDeathForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(ClientDeathForm formBean, FormBeanGroup group) {
        List<FormError> formErrors = super.validate(formBean, group);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(dependentValidator().validate(formValidator.getPatient(formBean.getMotechId()), group.getFormBeans(),
                new ExistsInDb().onFailure(new RegClientFormSubmittedInSameUpload())));
        return formErrors;
    }

    public DependentValidator dependentValidator() {
        return new DependentValidator();
    }
}
