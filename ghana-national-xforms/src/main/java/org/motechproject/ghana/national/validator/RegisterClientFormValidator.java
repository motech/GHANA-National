package org.motechproject.ghana.national.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;
import org.motechproject.ghana.national.validator.patient.AlwaysValid;
import org.motechproject.ghana.national.validator.patient.ExistsInDb;
import org.motechproject.ghana.national.validator.patient.IsFormSubmittedForAChild;
import org.motechproject.ghana.national.validator.patient.IsFormSubmittedForAFemale;
import org.motechproject.ghana.national.validator.patient.IsFormSubmittedWithValidHistoryDates;
import org.motechproject.ghana.national.validator.patient.NotExistsInDb;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import org.motechproject.ghana.national.validator.patient.RegClientFormSubmittedInSameUploadForMotechId;
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
import java.util.Date;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

@Component
public class RegisterClientFormValidator extends FormValidator<RegisterClientForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private MobileMidwifeValidator mobileMidwifeValidator;


    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(RegisterClientForm formBean, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(formBean, group, allForms);

        Patient patient = null;
        if(formBean.getMotechId() != null)
            patient = formValidator.getPatient(formBean.getMotechId());


        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));

        PatientValidator validators = patientValidator(formBean, formBean.getRegistrationMode(), formBean.getDateOfBirth(),
                formBean.getSex(), formBean.getRegistrantType(), formBean.getMotherMotechId());
        List<FormError> patientValidationErrors = getDependentValidator().validate(patient, group.getFormBeans(), allForms, validators);
        formErrors.addAll(patientValidationErrors);
        formErrors.addAll(formValidator.validateNHISExpiry(formBean.getNhisExpires()));
        formErrors.addAll(validateMobileMidwifeIfEnrolled(formBean));
        formErrors.addAll(validateIfParentIsNotSame(formBean));
        return formErrors;
    }

    public List<FormError> validateIfParentIsNotSame(RegisterClientForm registerClientForm) {
        if(PatientType.CHILD_UNDER_FIVE.equals(registerClientForm.getRegistrantType()) &&
                RegistrationType.USE_PREPRINTED_ID.equals(registerClientForm.getRegistrationMode()) &&
                registerClientForm.getMotechId().equals(registerClientForm.getMotherMotechId())) {
                    return new ArrayList<FormError>() {{
                        add(new FormError("motherMotechId", Constants.MOTHER_CHILD_SAME_ERROR_MSG));
                    }};
        }
        return new ArrayList<FormError>();
    }

    public PatientValidator patientValidator(FormWithHistoryInput formWithHistoryInput, RegistrationType registrationModeFromForm, Date dateOfBirthFromForm, String sexFromForm, PatientType registrantTypeFromForm, String motherMotechIdFromForm) {
        Patient patientsMother = null;
        if(motherMotechIdFromForm != null)
            patientsMother = formValidator.getPatient(motherMotechIdFromForm);

        String mothersMotechId = "Mothers motech Id";

        PatientValidator validators = new AlwaysValid();
        //do not inline
        validators
            .onSuccess(true, new NotExistsInDb(), RegistrationType.USE_PREPRINTED_ID.equals(registrationModeFromForm))
            .onSuccess(true, new IsFormSubmittedForAChild(dateOfBirthFromForm, null), PatientType.CHILD_UNDER_FIVE.equals(registrantTypeFromForm))
            .onSuccess(true, new IsFormSubmittedForAFemale(sexFromForm), PatientType.PREGNANT_MOTHER.equals(registrantTypeFromForm))
            .onSuccess(new IsFormSubmittedWithValidHistoryDates(dateOfBirthFromForm, formWithHistoryInput))
            .onSuccess(true, new ExistsInDb(patientsMother, new FormError(mothersMotechId, NOT_FOUND))
                    .onFailure(new RegClientFormSubmittedInSameUploadForMotechId(motherMotechIdFromForm, new FormError(mothersMotechId, NOT_FOUND))), PatientType.CHILD_UNDER_FIVE.equals(registrantTypeFromForm) && !StringUtils.isEmpty(motherMotechIdFromForm));

        return validators;
    }

    private List<FormError> validateMobileMidwifeIfEnrolled(RegisterClientForm formBean) {
        MobileMidwifeEnrollment midwifeEnrollment = formBean.createMobileMidwifeEnrollment(formBean.getMotechId());
        return midwifeEnrollment != null ? mobileMidwifeValidator.validateTime(midwifeEnrollment) : Collections.<FormError>emptyList();
    }

    DependentValidator getDependentValidator() {
        return new DependentValidator();
    }

}

