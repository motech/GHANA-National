package org.motechproject.ghana.national.validator.patient;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.is;

public class IsFormSubmittedWithValidHistoryDates extends PatientValidator {

    private FormWithHistoryInput formWithHistoryInput;
    private Date dateOfBirth;

    public IsFormSubmittedWithValidHistoryDates(FormWithHistoryInput formWithHistoryInput) {
        this.formWithHistoryInput = formWithHistoryInput;
    }

    public IsFormSubmittedWithValidHistoryDates(Date dateOfBirth, FormWithHistoryInput formWithHistoryInput) {
        this(formWithHistoryInput);
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        DateTime dateOfBirth = this.dateOfBirth != null? DateUtil.newDateTime(this.dateOfBirth): getDateOfBirthFromRegistrationForm(formsSubmittedWithinGroup);
        return new HasValidHistoryDates(formWithHistoryInput).historyDatesAfterDateOfBirth(dateOfBirth);
    }

    private DateTime getDateOfBirthFromRegistrationForm(List<FormBean> formsSubmittedWithinGroup) {
        RegisterClientForm registerPatientForm = (RegisterClientForm) filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmittedWithinGroup).get(0);
        return registerPatientForm.getDateOfBirth() != null ? DateUtil.newDateTime(registerPatientForm.getDateOfBirth()) : null;
    }
}
