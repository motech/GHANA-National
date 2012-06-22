package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.validator.FormValidator;
import org.motechproject.ghana.national.validator.RegisterCWCFormValidator;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.CwcFormMapper;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCControllerTest {
    CWCController cwcController;

    @Mock
    PatientService mockPatientService;

    @Mock
    FacilityHelper mockFacilityHelper;

    @Mock
    RegisterCWCFormValidator mockregisterCWCFormValidator;

    @Mock
    CareService mockCareService;

    @Mock
    CwcFormMapper mockCwcFormMapper;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    FormValidator mockFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        cwcController = new CWCController();
        ReflectionTestUtils.setField(cwcController, "patientService", mockPatientService);
        ReflectionTestUtils.setField(cwcController, "facilityHelper", mockFacilityHelper);
        ReflectionTestUtils.setField(cwcController, "careService", mockCareService);
        ReflectionTestUtils.setField(cwcController, "cwcFormMapper", mockCwcFormMapper);
        ReflectionTestUtils.setField(cwcController, "registerCWCFormValidator", mockregisterCWCFormValidator);
        ReflectionTestUtils.setField(cwcController, "formValidator", mockFormValidator);
        ReflectionTestUtils.setField(cwcController, "allEncounters", mockAllEncounters);
    }

    @Test
    public void shouldShowRegisterCWCForm() {
        ModelMap modelMap = new ModelMap();
        String motechPatientId = "1232132";
        final Object value1 = new Object();
        final Object value2 = new Object();
        final Object value3 = new Object();

        final Patient mockPatient = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(motechPatientId)).thenReturn(mockPatient);
        when(mockPatientService.getAgeOfPatientByMotechId(motechPatientId)).thenReturn(2);
        when(mockFacilityHelper.locationMap()).thenReturn(new HashMap<String, Object>() {{
            put("test3", value3);
        }});
        when(mockCwcFormMapper.setViewAttributes()).thenReturn(new HashMap<String, Object>() {{
            put("test1", value1);
            put("test2", value2);
        }});

        String actualResult = cwcController.create(motechPatientId, modelMap);

        assertThat(actualResult, is(CWCController.ENROLL_CWC_URL));
        verify(mockCwcFormMapper).setViewAttributes();
        verify(mockFacilityHelper).locationMap();
        assertThat(modelMap.get("test1"), is(value1));
        assertThat(modelMap.get("test2"), is(value2));
        assertThat(modelMap.get("test3"), is(value3));
    }

    @Test
    public void shouldThrowErrorWhenThePatientIsNotFoundWhileRenderingCWCPage() {
        String motechPatientId = "wewe";
        ModelMap modelMap = new ModelMap();
        when(mockPatientService.getPatientByMotechId(motechPatientId)).thenReturn(null);

        final String actualResult = cwcController.create(motechPatientId, modelMap);
        assertThat((List<String>) modelMap.get("errors"), hasItem(CWCController.PATIENT_NOT_FOUND));
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
        assertThat((List<String>) modelMap.get("errors"), hasItem(CWCController.PATIENT_IS_NOT_A_CHILD));
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
        final Date lastRotavirusDate = new Date(2011, 9, 6);
        final Date lastPneumococcalDate = new Date(2011, 9, 6);
        final String staffId = "456";
        final int lastIPTi = 1;
        final int lastPenta = 1;
        final int lastRotavirus = 1;
        final int lastPneumococcal = 1;
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
        cwcEnrollmentForm.setLastRotavirus(lastRotavirus);
        cwcEnrollmentForm.setLastRotavirusDate(lastRotavirusDate);
        cwcEnrollmentForm.setLastPneumococcal(lastPneumococcal);
        cwcEnrollmentForm.setLastPneumococcalDate(lastPneumococcalDate);
        cwcEnrollmentForm.setRegistrationToday(RegistrationToday.IN_PAST);


        when(mockFormValidator.validateIfStaffExists(staffId)).thenReturn(Collections.<FormError>emptyList());
        when(mockregisterCWCFormValidator.validatePatient(patientMotechId, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(Collections.<FormError>emptyList());

        cwcController.save(cwcEnrollmentForm, modelMap);
        final ArgumentCaptor<CwcVO> captor = ArgumentCaptor.forClass(CwcVO.class);
        verify(mockCareService).enroll(captor.capture());
        final CwcVO cwcVO = captor.getValue();

        assertThat(staffId, is(cwcVO.getStaffId()));
        assertThat(facilityId, is(cwcVO.getFacilityId()));
        assertThat(registartionDate, is(cwcVO.getRegistrationDate()));
        assertThat(patientMotechId, is(cwcVO.getPatientMotechId()));
        assertThat(lastBCGDate, is(cwcVO.getCWCCareHistoryVO().getBcgDate()));
        assertThat(lastVitADate, is(cwcVO.getCWCCareHistoryVO().getVitADate()));
        assertThat(lastMeaslesDate, is(cwcVO.getCWCCareHistoryVO().getMeaslesDate()));
        assertThat(lastYfDate, is(cwcVO.getCWCCareHistoryVO().getYfDate()));
        assertThat(lastPentaDate, is(cwcVO.getCWCCareHistoryVO().getLastPentaDate()));
        assertThat(lastPenta, is(cwcVO.getCWCCareHistoryVO().getLastPenta()));
        assertThat(lastOPVDate, is(cwcVO.getCWCCareHistoryVO().getLastOPVDate()));
        assertThat(lastOPV, is(cwcVO.getCWCCareHistoryVO().getLastOPV()));
        assertThat(lastRotavirus, is(cwcVO.getCWCCareHistoryVO().getLastRotavirus()));
        assertThat(lastPneumococcal, is(cwcVO.getCWCCareHistoryVO().getLastPneumococcal()));
        assertThat(lastIPTiDate, is(cwcVO.getCWCCareHistoryVO().getLastIPTiDate()));
        assertThat(lastRotavirusDate, is(cwcVO.getCWCCareHistoryVO().getLastRotavirusDate()));
        assertThat(lastPneumococcalDate, is(cwcVO.getCWCCareHistoryVO().getLastPneumococcalDate()));
        verify(mockCwcFormMapper).setViewAttributes();
        verify(mockFacilityHelper).locationMap();
    }

    @Test
    public void shouldReturnErrorIfThereAreFormValidations() {
        String motechId = "234543";
        String staffId = "11";
        String facilityId = "43";
        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();
        cwcEnrollmentForm.setPatientMotechId(motechId);
        cwcEnrollmentForm.setStaffId(staffId);
        final FacilityForm facilityForm = new FacilityForm();
        facilityForm.setFacilityId(facilityId);
        cwcEnrollmentForm.setFacilityForm(facilityForm);
        final ModelMap modelMap = new ModelMap();

        when(mockregisterCWCFormValidator.validatePatient(motechId, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(
                new ArrayList<FormError>() {{
                    add(new FormError(Constants.CHILD_AGE_PARAMETER, "description1"));
                    add(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME, "description2"));
                }});

        when(mockFormValidator.validateIfStaffExists(staffId)).thenReturn(
                new ArrayList<FormError>() {{
                    add(new FormError(FormValidator.STAFF_ID, "description3"));
                }});

        final String result = cwcController.save(cwcEnrollmentForm, modelMap);

        assertThat((List<String>) modelMap.get("errors"), hasItems(CWCController.PATIENT_IS_NOT_A_CHILD,
                "Patient Description2", CWCController.STAFF_ID_NOT_FOUND));
        assertThat(result, is(equalTo(CWCController.ENROLL_CWC_URL)));
        verifyZeroInteractions(mockCareService);
        verify(mockCwcFormMapper).setViewAttributes();
        verify(mockFacilityHelper).locationMap();
    }
}                   
