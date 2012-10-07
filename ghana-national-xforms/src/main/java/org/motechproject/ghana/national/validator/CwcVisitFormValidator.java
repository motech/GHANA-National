package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.CWCVisitForm;
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
    public List<FormError> validate(CWCVisitForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> errors = super.validate(formBean, group, allForms);
        errors.addAll(businessValidations(formBean, group.getFormBeans(), allForms));
        return errors;
    }

    List<FormError> businessValidations(CWCVisitForm formBean, List<FormBean> formsWithinGroup, List<FormBean> allForms) {
        List<FormError> errors = new ArrayList<FormError>();
        errors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        errors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));

        if (!formBean.getVisitor()) {
            Patient patient = formValidator.getPatient(formBean.getMotechId());
            errors.addAll(dependentValidator().validate(patient, formsWithinGroup,
                    allForms, new ExistsInDb()
                    .onSuccess(new IsAlive()
                            .onSuccess(new IsAChild()
                                    .onSuccess(new EnrolledToCWC(allEncounters)
                                            .onFailure(new RegCWCFormSubmittedInSameUpload()))))
                    .onFailure(new RegCWCFormSubmittedInSameUpload()
                            .onFailure(new RegClientFormSubmittedInSameUpload()
                                    .onSuccess(new RegClientFormSubmittedForChild())))));
        }
        return errors;
    }

    public DependentValidator dependentValidator() {
        return new DependentValidator();
    }
}
