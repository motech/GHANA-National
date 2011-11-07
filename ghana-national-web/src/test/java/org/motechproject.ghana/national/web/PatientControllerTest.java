package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientControllerTest {
    PatientController patientController;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    PatientService mockPatientService;
    @Mock
    MessageSource mockMessageSource;

    @Before
    public void setUp() {
        initMocks(this);
        patientController = new PatientController(mockFacilityService, mockPatientService, mockMessageSource);
    }

    @Test
    public void shouldRenderPageToCreateAPatient() {
        final ModelMap modelMap = new ModelMap();
        final String key = "key";
        when(mockFacilityService.locationMap()).thenReturn(new HashMap<String, Object>() {{
            put(key, new Object());
        }});
        patientController.newPatientForm(modelMap);
        verify(mockFacilityService).locationMap();
        assertNotNull(modelMap.get(key));
    }
}
