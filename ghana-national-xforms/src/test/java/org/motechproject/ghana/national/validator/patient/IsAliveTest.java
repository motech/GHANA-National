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

import static org.junit.Assert.assertEquals;

public class IsAliveTest {
    @Test
    public void shouldValidateThatPatientIsAlive(){
        MRSPatient mrsPatient = new MRSPatient("motechId",new MRSPerson().dead(false),new MRSFacility("facilityId"));
        Patient patient = new Patient(mrsPatient);
        PatientValidator validator = new IsAlive();
        List<FormError> formErrors = validator.validate(patient, Collections.<FormBean>emptyList());
        assertEquals(0,formErrors.size());

        patient.getMrsPatient().getPerson().dead(true);
        formErrors = validator.validate(patient, Collections.<FormBean>emptyList());
        assertEquals(Arrays.asList(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME,Constants.IS_NOT_ALIVE)),formErrors);
    }
}
