package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.CWCService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCControllerTest {
    CWCController cwcController;

    @Mock
    PatientService mockPatientService;

    @Mock
    FacilityHelper mockFacilityHelper;

    @Mock
    CWCService mockCwcService;
    
    @Mock
    StaffService mockStaffService;

    @Before
    public void setUp() {
        initMocks(this);
        cwcController = new CWCController();
        ReflectionTestUtils.setField(cwcController, "patientService", mockPatientService);
        ReflectionTestUtils.setField(cwcController, "facilityHelper", mockFacilityHelper);
        ReflectionTestUtils.setField(cwcController, "cwcService", mockCwcService);
        ReflectionTestUtils.setField(cwcController, "staffService", mockStaffService);

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
        assertThat((Map<Integer, String>) modelMap.get(CWCController.LAST_IPTI), allOf(
                hasEntry(1, CWCController.IPTI_1),
                hasEntry(2, CWCController.IPTI_2),
                hasEntry(3, CWCController.IPTI_3)
        ));
        assertThat((Map<Integer, String>) modelMap.get(CWCController.LAST_OPV), allOf(
                hasEntry(0, CWCController.OPV_0),
                hasEntry(1, CWCController.OPV_1),
                hasEntry(2, CWCController.OPV_2),
                hasEntry(3, CWCController.OPV_3)
        ));
        assertThat((Map<Integer, String>) modelMap.get(CWCController.LAST_PENTA), allOf(
                hasEntry(1, CWCController.PENTA_1),
                hasEntry(2, CWCController.PENTA_2),
                hasEntry(3, CWCController.PENTA_3)
        ));
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
        Patient mockPatient = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(motechPatientId)).thenReturn(mockPatient);
        when(mockPatientService.getAgeOfPatientByMotechId(motechPatientId)).thenReturn(10);
        final String actualResult = cwcController.create(motechPatientId, modelMap);
        assertThat((String) modelMap.get("error"), is(CWCController.PATIENT_IS_NOT_A_CHILD));
        assertThat(actualResult, is(CWCController.ENROLL_CWC_URL));
    }

    @Test
    public void shouldSaveCWCEnrollmentForm() {
        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();

        final Date registartionDate = new Date(2011, 9, 1);
        final Date lastBCGDate = new Date(2011, 10, 1);
        final Date lastVitADate = new Date(2011, 11, 1);
        final Date lastMeaslesDate = new Date(2011, 9, 2);
        final Date lastYfDate = new Date(2011, 9, 3);
        final Date lastPentaDate = new Date(2011, 9, 4);
        final Date lastOPVDate = new Date(2011, 9, 5);
        final Date lastIPTiDate = new Date(2011, 9, 6);
        final String staffId = "456";
        final int lastIPTi = 1;
        final int lastPenta = 1;
        final String patientMotechId = "1234567";
        final int lastOPV = 0;
        ModelMap modelMap = new ModelMap();
        final String facilityId = "3232";

        cwcEnrollmentForm.setStaffId(staffId);
        final FacilityForm facilityForm = new FacilityForm();
        facilityForm.setFacilityId(facilityId);
        cwcEnrollmentForm.setFacilityForm(facilityForm);
        cwcEnrollmentForm.setRegistrationDate(registartionDate);
        cwcEnrollmentForm.setPatientMotechId(patientMotechId);
        cwcEnrollmentForm.setBcgDate(lastBCGDate);
        cwcEnrollmentForm.setVitADate(lastVitADate);
        cwcEnrollmentForm.setMeaslesDate(lastMeaslesDate);
        cwcEnrollmentForm.setYfDate(lastYfDate);
        cwcEnrollmentForm.setLastPentaDate(lastPentaDate);
        cwcEnrollmentForm.setLastPenta(lastPenta);
        cwcEnrollmentForm.setLastOPVDate(lastOPVDate);
        cwcEnrollmentForm.setLastOPV(lastOPV);
        cwcEnrollmentForm.setLastIPTiDate(lastIPTiDate);
        cwcEnrollmentForm.setLastIPTi(lastIPTi);
        final MRSUser mrsUser = mock(MRSUser.class);
        when(mockStaffService.getUserById(staffId)).thenReturn(mrsUser);
        
        cwcController.save(cwcEnrollmentForm, modelMap);
        final ArgumentCaptor<CwcVO> captor = ArgumentCaptor.forClass(CwcVO.class);
        verify(mockCwcService).enroll(captor.capture());
        final CwcVO cwcVO = captor.getValue();

        assertThat(staffId, is(cwcVO.getStaffId()));
        assertThat(facilityId, is(cwcVO.getFacilityId()));
        assertThat(registartionDate, is(cwcVO.getRegistrationDate()));
        assertThat(patientMotechId, is(cwcVO.getPatientMotechId()));
        assertThat(lastBCGDate, is(cwcVO.getBcgDate()));
        assertThat(lastVitADate, is(cwcVO.getVitADate()));
        assertThat(lastMeaslesDate, is(cwcVO.getMeaslesDate()));
        assertThat(lastYfDate, is(cwcVO.getYfDate()));
        assertThat(lastPentaDate, is(cwcVO.getLastPentaDate()));
        assertThat(lastPenta, is(cwcVO.getLastPenta()));
        assertThat(lastOPVDate, is(cwcVO.getLastOPVDate()));
        assertThat(lastOPV, is(cwcVO.getLastOPV()));
        assertThat(lastIPTiDate, is(cwcVO.getLastIPTiDate()));
    }
}                   
