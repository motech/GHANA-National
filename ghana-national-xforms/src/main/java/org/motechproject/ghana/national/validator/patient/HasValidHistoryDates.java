package org.motechproject.ghana.national.validator.patient;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

public class HasValidHistoryDates extends PatientValidator {

    private FormWithHistoryInput formWithHistoryInput;

    public HasValidHistoryDates(FormWithHistoryInput formWithHistoryInput) {
        this.formWithHistoryInput = formWithHistoryInput;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        DateTime dateOfBirth = patient.dateOfBirth();
        return historyDatesAfterDateOfBirth(dateOfBirth);

    }

    public List<FormError> historyDatesAfterDateOfBirth(DateTime dateOfBirth) {
        List<FormError> formErrors = new ArrayList<>();
        if (dateOfBirth != null) {
            HashMap<String, Date> historyDatesMap = formWithHistoryInput.getHistoryDatesMap();
            for (String vaccineDate : historyDatesMap.keySet()) {
                if (historyDatesMap.get(vaccineDate) != null && !DateUtil.isOnOrAfter(DateUtil.newDateTime(historyDatesMap.get(vaccineDate)), dateOfBirth))
                    formErrors.add(new FormError(vaccineDate, Constants.AFTER_DOB));
            }
        }
        return formErrors;
    }

}
