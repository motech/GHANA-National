package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.PatientService;
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
import java.util.Collections;
import java.util.List;

@Component
public class RegisterCWCFormValidator extends FormValidator<RegisterCWCForm> {

    @Autowired
    private PatientService patientService;

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private MobileMidwifeValidator mobileMidwifeValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterCWCForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(validateMobileMidwifeIfEnrolled(formBean));

        formErrors.addAll(validatePatient(formBean.getMotechId(), group.getFormBeans(), allForms));

        return formErrors;
    }

    public List<FormError> validatePatient(String motechId, List<FormBean> formBeans, List<FormBean> allForms) {
        List<FormError> formErrors = new ArrayList<FormError>();
        final Patient patient = formValidator.getPatient(motechId);
        final PatientValidator regClientFormValidators = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForChild(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.CHILD_AGE_MORE_ERR_MSG)));
        final List<FormError> errors = new DependentValidator().validate(patient, formBeans, allForms, new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsAChild())).onFailure(regClientFormValidators));
        formErrors.addAll(errors);
        return formErrors;
    }

    private List<FormError> validateMobileMidwifeIfEnrolled(RegisterCWCForm formBean) {
        MobileMidwifeEnrollment midwifeEnrollment = formBean.createMobileMidwifeEnrollment();
        return midwifeEnrollment != null ? mobileMidwifeValidator.validateTime(midwifeEnrollment) : Collections.<FormError>emptyList();
    }

}
