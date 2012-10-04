package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.mapper.OutPatientVisitMapper;
import org.motechproject.ghana.national.repository.AllMotechModuleOutPatientVisits;
import org.motechproject.ghana.national.validator.patient.ExistsInDb;
import org.motechproject.ghana.national.validator.patient.IsAChild;
import org.motechproject.ghana.national.validator.patient.IsAlive;
import org.motechproject.ghana.national.validator.patient.IsFemale;
import org.motechproject.ghana.national.validator.patient.RegClientFormSubmittedInSameUpload;
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

import static org.motechproject.ghana.national.domain.Constants.IS_DUPLICATE;

@Component
public class OutPatientVisitFormValidator extends FormValidator<OutPatientVisitForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Autowired
    private AllMotechModuleOutPatientVisits allMotechModuleOutPatientVisits;

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(OutPatientVisitForm outPatientVisitForm, FormBeanGroup group, List<FormBean> allForms) {
        List<FormError> formErrors = super.validate(outPatientVisitForm, group, allForms);
        formErrors.addAll(formValidator.validateIfStaffExists(outPatientVisitForm.getStaffId()));
        formErrors.addAll(formValidator.validateIfFacilityExists(outPatientVisitForm.getFacilityId()));
        formErrors.addAll(validateIfDuplicateFormExists(outPatientVisitForm));
        if (Boolean.TRUE.equals(outPatientVisitForm.isRegistered())) {
            Patient patient = formValidator.getPatient(outPatientVisitForm.getMotechId());
            formErrors.addAll(new DependentValidator().validate(patient, group.getFormBeans(),
                    allForms, new ExistsInDb()
                    .onSuccess(new IsAlive()
                            .onSuccess(false, new IsAChild(), PatientType.CHILD_UNDER_FIVE.equals(outPatientVisitForm.getRegistrantType()))
                            .onSuccess(false, new IsFemale(), PatientType.PREGNANT_MOTHER.equals(outPatientVisitForm.getRegistrantType())))
                    .onFailure(new RegClientFormSubmittedInSameUpload())));
        }
        return formErrors;
    }

    private List<FormError> validateIfDuplicateFormExists(OutPatientVisitForm outPatientVisitForm) {
        if (allMotechModuleOutPatientVisits.isDuplicate(new OutPatientVisitMapper().map(outPatientVisitForm))) {
            return new ArrayList<FormError>() {{
                add(new FormError("OPV Form", IS_DUPLICATE));
            }};
        }
        return new ArrayList<>();
    }
}
