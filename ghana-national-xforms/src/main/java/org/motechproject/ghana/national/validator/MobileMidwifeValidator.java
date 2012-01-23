package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Component
public class MobileMidwifeValidator {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    final static String PATIENT_ID_ATTRIBUTE_NAME = "patientId";

    public List<FormError> validate(MobileMidwifeEnrollment enrollment) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(validateFacilityPatientAndStaff(enrollment));
        formErrors.addAll(validateFieldValues(enrollment));
        return formErrors;
    }

    public List<FormError> validateFieldValues(MobileMidwifeEnrollment enrollment) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(validateTime(enrollment));
        return formErrors;
    }

    public List<FormError> validateForIncludeForm(MobileMidwifeEnrollment enrollment) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(validateFieldValues(enrollment));
        return formErrors;
    }

    private List<FormError> validateFacilityPatientAndStaff(MobileMidwifeEnrollment enrollment) {
        List<FormError> formErrors = new ArrayList<FormError>();
        formErrors.addAll(formValidator.validatePatient(enrollment.getPatientId(), PATIENT_ID_ATTRIBUTE_NAME));
        formErrors.addAll(formValidator.validateIfFacilityExists(enrollment.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(enrollment.getStaffId()));
        return formErrors;
    }

    List<FormError> validateTime(MobileMidwifeEnrollment enrollment) {

        if (Medium.VOICE.equals(enrollment.getMedium())) {
            Time maxTime = Constants.MOBILE_MIDWIFE_MAX_TIMEOFDAY_FOR_VOICE;
            Time minTime = Constants.MOBILE_MIDWIFE_MIN_TIMEOFDAY_FOR_VOICE;

            Time timeOfDay = enrollment.getTimeOfDay();
            if (!timeOfDay.ge(minTime) || !timeOfDay.le(maxTime)) {
                return asList(new FormError("", Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE));
            }
        }
        return emptyList();
    }


}
