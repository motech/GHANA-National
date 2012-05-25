package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
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
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_MORE_ERR_MSG;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_PARAMETER;

public class IsAChildTest {
    @Test
    public void shouldValidateThatPatientIsFemale(){
        Patient patient = new Patient(new MRSPatient("motechId",new MRSPerson().age(3),new MRSFacility("facilityId")));
        PatientValidator validator = new IsAChild();
        List<FormError> formErrors = validator.validate(patient, Collections.<FormBean>emptyList());
        assertEquals(0,formErrors.size());

        patient.getMrsPatient().getPerson().age(45);
        formErrors = validator.validate(patient,Collections.<FormBean>emptyList());
        assertEquals(Arrays.asList(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)),formErrors);

    }
}
