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
    private FormError formError;

    public  IsFormSubmittedForAChild(Date dateOfBirth, FormError formError) {
        this.dateOfBirth = dateOfBirth;
        this.formError = formError;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        return DateUtil.isOnOrBefore(DateUtil.newDate(dateOfBirth).toDateTimeAtStartOfDay(),DateUtil.today().minusYears(5).toDateTimeAtStartOfDay()) ?
                getFormError() : new ArrayList<FormError>();
    }

    private List<FormError> getFormError() {
        return formError == null ? Arrays.asList(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)) : Arrays.asList(formError);
    }
}
