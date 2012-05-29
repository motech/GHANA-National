package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
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
    @Autowired
    private AllEncounters allEncounters;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(DeliveryNotificationForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        final Patient patient = formValidator.getPatient(formBean.getMotechId());
        formErrors.addAll(dependentValidator().validate(patient, group.getFormBeans(),
                allForms, new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale().onSuccess(new AgeMoreThan(5).onSuccess(new EnrolledToANC(allEncounters).onFailure(new RegANCFormSubmittedInSameUpload())))))
                                .onFailure(new RegANCFormSubmittedInSameUpload()
                                        .onFailure(new RegCWCFormSubmittedInSameUpload()
                                                .onSuccess(new RegClientFormSubmittedForMother())))));
        return formErrors;
    }

    public DependentValidator dependentValidator() {
        return new DependentValidator();
    }
}
