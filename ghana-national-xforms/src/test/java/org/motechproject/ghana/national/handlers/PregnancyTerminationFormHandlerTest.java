package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.FORM_BEAN;

public class PregnancyTerminationFormHandlerTest {
    private PregnancyTerminationFormHandler pregnancyTerminationFormHandler;

    @Mock
    private PatientService mockPatientService;

    @Mock
    private EncounterService mockEncounterService;

    @Before
    public void setUp() {
        initMocks(this);
        pregnancyTerminationFormHandler = new PregnancyTerminationFormHandler();
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler, "encounterService", mockEncounterService);
    }



    private PregnancyTerminationForm pregnancyTerminationForm(String motechId, String staffId, String facilityId) {
        PregnancyTerminationForm pregnancyTerminationForm = new PregnancyTerminationForm();
        pregnancyTerminationForm.setFacilityId(facilityId);
        pregnancyTerminationForm.setMotechId(motechId);
        pregnancyTerminationForm.setStaffId(staffId);
        return pregnancyTerminationForm;
    }
}
