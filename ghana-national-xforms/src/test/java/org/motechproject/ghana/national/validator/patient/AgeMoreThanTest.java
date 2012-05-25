package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AgeMoreThanTest {
    PatientValidator validator;

    @Test
    public void shouldValidateIfAgeIsLessThanRequired(){
        Patient patient = new Patient(new MRSPatient("motechId",new MRSPerson().age(10),new MRSFacility("facilityId")));
        PatientValidator validator = new AgeMoreThan(12);
        final List<FormBean> formsSubmitted = new ArrayList<FormBean>();
        List<FormError> formErrors = validator.validate(patient, formsSubmitted);
        assertEquals(Arrays.asList(new FormError("Patient age", "is less than 12")),formErrors);
    }
}
