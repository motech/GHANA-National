package org.motechproject.ghana.national.validator.patient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;

public class EnrolledToANCTest {
    @Mock
    private AllEncounters allEncounters;

    PatientValidator validator ;

    @Before
    public void setup() {
        initMocks(this);
        validator = new EnrolledToANC(allEncounters);
    }
    
    @Test
    public void shouldVerifyIfPatientEnrolledForANC(){
        Patient patient = new Patient(new MRSPatient("motechId",new MRSPerson(),new MRSFacility("facilityId")));
        when(allEncounters.getLatest("motechId", ANC_REG_VISIT.value())).thenReturn(null);
        List<FormError> errors = validator.validate(patient, Collections.<FormBean>emptyList());
        assertThat(errors,hasItem(new FormError(patient.getMotechId(), "not registered for ANC")));

        MRSEncounter mrsEncounter = mock(MRSEncounter.class);
        when(allEncounters.getLatest("motechId",ANC_REG_VISIT.value())).thenReturn(mrsEncounter);
        errors = validator.validate(patient, Collections.<FormBean>emptyList());
        assertThat(errors,not(hasItem(new FormError(patient.getMotechId(), "not registered for ANC"))));
    }

}
