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

public class HistoryDateValidator extends PatientValidator{

    private FormWithHistoryInput formWithHistoryInput;

    public HistoryDateValidator(FormWithHistoryInput formWithHistoryInput) {
        this.formWithHistoryInput = formWithHistoryInput;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {
        List<FormBean> filteredForms = filter(having(on(FormBean.class).getFormname(), is("registerPatient")), formsSubmittedWithinGroup);
        RegisterClientForm registerPatientForm = null;
        if (!filteredForms.isEmpty())
            registerPatientForm = (RegisterClientForm) filteredForms.get(0);
        if(patient ==null && registerPatientForm == null)
            return Collections.emptyList();
        DateTime dateOfBirth = DateUtil.newDateTime((patient == null ) ? registerPatientForm.getDateOfBirth() : patient.dateOfBirth().toDate());

        List<FormError> formErrors = new ArrayList<>();
        HashMap<String, Date> historyDatesMap = formWithHistoryInput.getHistoryDatesMap();
        for (String vaccineDate : historyDatesMap.keySet()) {
            if (historyDatesMap.get(vaccineDate) != null && !DateUtil.isOnOrAfter(DateUtil.newDateTime(historyDatesMap.get(vaccineDate)), dateOfBirth))
                formErrors.add(new FormError(vaccineDate, Constants.AFTER_DOB));
        }
        return formErrors;
    }
}
