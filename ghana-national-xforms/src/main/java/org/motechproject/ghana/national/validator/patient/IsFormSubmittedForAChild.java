package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_MORE_ERR_MSG;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_PARAMETER;

public class IsFormSubmittedForAChild extends PatientValidator {
    private Date dateOfBirth;

    public IsFormSubmittedForAChild(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        return DateUtil.getDifferenceOfDatesInYears(dateOfBirth) > 5 ?
                Arrays.asList(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)) : new ArrayList<FormError>();
    }
}
