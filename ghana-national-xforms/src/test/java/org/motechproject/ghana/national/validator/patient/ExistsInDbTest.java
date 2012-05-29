package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class ExistsInDbTest {
    @Test
    public void shouldValidateIfPatientExistsInDb(){
        Patient patient = null;
        final PatientValidator validator = new ExistsInDb();
        final List<FormBean> formsSubmitted = new ArrayList<FormBean>();
        List<FormError> formErrors = validator.validate(patient, formsSubmitted, formsSubmitted);
        assertEquals(Arrays.asList(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME,NOT_FOUND)),formErrors);

        patient = new Patient(new MRSPatient("motechId"));
        formErrors = validator.validate(patient,formsSubmitted, formsSubmitted);
        assertEquals(0,formErrors.size());
    }
}
