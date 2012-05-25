package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class IsFemaleTest {
    @Test
    public void shouldValidateThatPatientIsFemale(){
        Patient patient = new Patient(new MRSPatient("motechId",new MRSPerson().gender("F"),new MRSFacility("facilityId")));
        PatientValidator validator = new IsFemale();
        List<FormError> formErrors = validator.validate(patient, Collections.<FormBean>emptyList());
        assertEquals(0,formErrors.size());

        patient.getMrsPatient().getPerson().gender("M");
        formErrors = validator.validate(patient,Collections.<FormBean>emptyList());
        assertEquals(Arrays.asList(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME,Constants.GENDER_ERROR_MSG)),formErrors);

    }
}
