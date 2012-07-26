package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class RegClientFormSubmittedInSameUploadForMotechId extends PatientValidator {
    private String motechId;
    private FormError formError;

    public RegClientFormSubmittedInSameUploadForMotechId(String motechId, FormError formError) {
        this.motechId = motechId;
        this.formError = formError;
    }

    @Override
    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms) {

        for (FormBean formBean : allForms) {
            if(motechId!=null && "registerPatient".equals(formBean.getFormname())){
                RegisterClientForm registerClientForm = (RegisterClientForm) formBean;
                if(registerClientForm.getMotechId().equals(motechId))
                    return Collections.emptyList();
            }
        }
        return Arrays.asList(getFormError());
    }

    private FormError getFormError() {
        return (formError==null) ? new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND) : formError;
    }
}
