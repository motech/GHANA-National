package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCControllerTest {
    CWCController cwcController;

    @Mock
    PatientService mockPatientService;
    
    @Before
    public void setUp() {
        initMocks(this);
        cwcController = new CWCController();
        ReflectionTestUtils.setField(cwcController, "patientService", mockPatientService);

    }
    
    @Test
    public void shouldShowRegisterCWCForm() {
        ModelMap modelMap = new ModelMap();
        String motechPatientId = "1232132";
        final Patient mockPatient = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(motechPatientId)).thenReturn(mockPatient);
        when(mockPatientService.getAgeOfPatientByMotechId(motechPatientId)).thenReturn(2);
        
        final String actualResult = cwcController.create(motechPatientId, modelMap);
        assertThat(actualResult, is(CWCController.ENROLL_CWC_URL));
        assertThat((List<CwcCareHistory>) modelMap.get(CWCController.CARE_HISTORIES), is(Arrays.asList(CwcCareHistory.values())));
        assertThat((List<String>) modelMap.get(CWCController.LAST_IP_TI), is(Arrays.asList(CWCController.IPTI_1,CWCController.IPTI_2,CWCController.IPTI_3)));
        assertThat((List<String>) modelMap.get(CWCController.LAST_OPV), is(Arrays.asList(CWCController.OPV_0,CWCController.OPV_1,CWCController.OPV_2,CWCController.OPV_3)));
        assertThat((List<String>) modelMap.get(CWCController.LAST_PENTA), is(Arrays.asList(CWCController.PENTA_1,CWCController.PENTA_2,CWCController.PENTA_3)));
    }
    
    @Test
    public void shouldThrowErrorWhenThePatientIsNotFoundWhileRenderingCWCPage() {
        String motechPatientId = "wewe";
        ModelMap modelMap = new ModelMap();
        when(mockPatientService.getPatientByMotechId(motechPatientId)).thenReturn(null);

        final String actualResult = cwcController.create(motechPatientId, modelMap);
        assertThat((String) modelMap.get("error"), is(CWCController.PATIENT_NOT_FOUND));
        assertThat(actualResult, is(CWCController.ENROLL_CWC_URL));
    }

    @Test
    public void shouldThrowErrorWhenThePatientIsNotAChild() {
        String motechPatientId = "wewe";
        ModelMap modelMap = new ModelMap();
        Patient mockPatient=mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(motechPatientId)).thenReturn(mockPatient);
        when(mockPatientService.getAgeOfPatientByMotechId(motechPatientId)).thenReturn(10);
        final String actualResult = cwcController.create(motechPatientId, modelMap);
        assertThat((String) modelMap.get("error"), is(CWCController.PATIENT_IS_NOT_A_CHILD));
        assertThat(actualResult, is(CWCController.ENROLL_CWC_URL));
    }
}
