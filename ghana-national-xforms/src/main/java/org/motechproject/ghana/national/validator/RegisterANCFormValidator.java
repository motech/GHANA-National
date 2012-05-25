package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RegisterANCFormValidator extends FormValidator<RegisterANCForm> {
    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private MobileMidwifeValidator mobileMidwifeValidator;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterANCForm formBean, FormBeanGroup group) {
        List<FormError> formErrors = super.validate(formBean, group);
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(validatePatient(formValidator.getPatient(formBean.getMotechId()), group.getFormBeans()));
        formErrors.addAll(validateMobileMidwifeIfEnrolled(formBean));
        return formErrors;
    }

    public List<FormError> validatePatient(Patient patient, List<FormBean> formsUploaded) {
        final PatientValidator regClientFormValidators = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForFemale());
        final PatientValidator validator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale())).onFailure(regClientFormValidators);
        return getDependentValidator().validate(patient, formsUploaded, validator);
    }

    public DependentValidator getDependentValidator() {
        return new DependentValidator();
    }

    private List<FormError> validateMobileMidwifeIfEnrolled(RegisterANCForm formBean) {
        MobileMidwifeEnrollment midwifeEnrollment = formBean.createMobileMidwifeEnrollment();
        return midwifeEnrollment != null ? mobileMidwifeValidator.validateForIncludeForm(midwifeEnrollment) : Collections.<FormError>emptyList();
    }

    public List<FormError> validatePatientAndStaff(String motechPatientId, String staffId) {
        List<FormError> errors = formValidator.validateIfStaffExists(staffId);
        errors.addAll(validatePatient(formValidator.getPatient(motechPatientId),Collections.<FormBean>emptyList()));
        return errors;
    }
}
