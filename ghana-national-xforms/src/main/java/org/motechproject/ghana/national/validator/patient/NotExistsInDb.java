package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;

public class NotExistsInDb extends PatientValidator{

    public List<FormError> validate(Patient patient, List<FormBean> formsSubmittedWithinGroup, List<FormBean> allForms){
        return (patient == null) ? Collections.<FormError>emptyList() : Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "in use"));
    }

}
