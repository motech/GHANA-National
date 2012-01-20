package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.model.Time;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MobileMidwifeFormValidator extends FormValidator<MobileMidwifeForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    final static String PATIENT_ID_ATTRIBUTE_NAME = "patientId";

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<FormError> validate(MobileMidwifeForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(validateFacilityPatientAndStaff(formBean.getPatientId(), formBean.getFacilityId(), formBean.getStaffId(),
                formBean.getMediumStripingOwnership(), formBean.getTimeOfDay()));        
        return formErrors;
    }

    public List<FormError> validateFacilityPatientAndStaff(String patientId, String facilityId, String staffId, Medium medium, Time time) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(formValidator.validatePatient(patientId, PATIENT_ID_ATTRIBUTE_NAME));
        formErrors.addAll(formValidator.validateIfFacilityExists(facilityId));
        formErrors.addAll(formValidator.validateIfStaffExists(staffId));
        addFormError(formErrors, validateTime(medium, time));
        return formErrors;
    }

    private void addFormError(List<FormError> formErrors, FormError formError) {
        if(formError != null) formErrors.add(formError);
    }

    private FormError validateTime(Medium medium, Time timeOfDay) {
        if (Medium.VOICE.equals(medium)) {
            Time maxTime = Constants.MOBILE_MIDWIFE_MAX_TIMEOFDAY_FOR_VOICE;
            Time minTime = Constants.MOBILE_MIDWIFE_MIN_TIMEOFDAY_FOR_VOICE;

            if (!timeOfDay.ge(minTime) || !timeOfDay.le(maxTime)) {
                return new FormError("", Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE);
            }
        }
        return null;
    }


}
