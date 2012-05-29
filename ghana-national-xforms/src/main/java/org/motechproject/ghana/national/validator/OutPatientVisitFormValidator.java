package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
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
public class OutPatientVisitFormValidator extends FormValidator<OutPatientVisitForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;


    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(OutPatientVisitForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        if (Boolean.FALSE.equals(formBean.isVisitor())) {
            Patient patient = formValidator.getPatient(formBean.getMotechId());
            formErrors.addAll(new DependentValidator().validate(patient, group.getFormBeans(),
                    allForms, new ExistsInDb()
                            .onSuccess(new IsAlive()
                                    .onSuccess(new IsAChild(), PatientType.CHILD_UNDER_FIVE.equals(formBean.getRegistrantType()))
                                    .onSuccess(new IsFemale(), PatientType.PREGNANT_MOTHER.equals(formBean.getRegistrantType())))
                            .onFailure(new RegClientFormSubmittedInSameUpload())));
        }
        return formErrors;
    }
}
