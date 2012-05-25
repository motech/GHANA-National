package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.CWCVisitForm;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class CwcVisitFormValidator extends FormValidator<CWCVisitForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private AllEncounters allEncounters;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(CWCVisitForm formBean, FormBeanGroup group) {
        List<FormError> errors = super.validate(formBean, group);
        errors.addAll(businessValidations(formBean, group));
        return errors;
    }

    List<FormError>  businessValidations(CWCVisitForm formBean,  FormBeanGroup group) {
        List<FormError> errors = new ArrayList<FormError>();
        errors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        errors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        Patient patient = formValidator.getPatient(formBean.getMotechId());
        errors.addAll(dependentValidator().validate(patient, group.getFormBeans(),
                new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsAChild().onSuccess(new EnrolledToCWC(allEncounters).onFailure(new RegCWCFormSubmittedInSameUpload()))))
                                .onFailure(new RegCWCFormSubmittedInSameUpload()
                                        .onFailure(new RegClientFormSubmittedInSameUpload()
                                                .onSuccess(new RegClientFormSubmittedForChild())))));
        return errors;
    }

    public DependentValidator dependentValidator() {
        return new DependentValidator();
    }
}
