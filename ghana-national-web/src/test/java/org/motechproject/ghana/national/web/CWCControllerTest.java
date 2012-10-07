package org.motechproject.ghana.national.web;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.validator.FormValidator;
import org.motechproject.ghana.national.validator.RegisterCWCFormValidator;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.CwcFormMapper;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCControllerTest {
    @InjectMocks
    CWCController cwcController = new CWCController();

    @Mock
    PatientService mockPatientService;

    @Mock
    FacilityHelper mockFacilityHelper;

    @Mock
    RegisterCWCFormValidator mockRegisterCWCFormValidator;

    @Mock
    CareService mockCareService;

    @Mock
    AllEncounters mockAllEncounters; //needs to be mocked.

    @Mock
    CwcFormMapper mockCwcFormMapper;

    @Mock
    FormValidator mockFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
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
        CWCEnrollmentForm cwcEnrollmentForm = createCWCEnrollmentForm();
        ModelMap modelMap = new ModelMap();

        when(mockFormValidator.validateIfStaffExists(cwcEnrollmentForm.getStaffId())).thenReturn(Collections.<FormError>emptyList());
        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(cwcEnrollmentForm.getPatientMotechId())).thenReturn(patient);
        when(patient.dateOfBirth()).thenReturn(DateUtil.newDate(2000, 12, 12).toDateTimeAtCurrentTime());
        when(mockRegisterCWCFormValidator.validatePatient(patient, cwcEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(Collections.<FormError>emptyList());
        cwcEnrollmentForm.setAddHistory(false);
        cwcController.save(cwcEnrollmentForm, modelMap);
        final ArgumentCaptor<CwcVO> captor = ArgumentCaptor.forClass(CwcVO.class);
        verify(mockCareService).enroll(captor.capture());
        final CwcVO cwcVO = captor.getValue();

        assertThat(cwcEnrollmentForm.getStaffId(), is(cwcVO.getStaffId()));
        assertThat(cwcEnrollmentForm.getFacilityForm().getFacilityId(), is(cwcVO.getFacilityId()));
        assertThat(cwcEnrollmentForm.getRegistrationDate(), is(cwcVO.getRegistrationDate()));
        assertThat(cwcEnrollmentForm.getPatientMotechId(), is(cwcVO.getPatientMotechId()));
        assertThat(cwcEnrollmentForm.getBcgDate(), is(cwcVO.getCWCCareHistoryVO().getBcgDate()));
        assertThat(cwcEnrollmentForm.getVitADate(), is(cwcVO.getCWCCareHistoryVO().getVitADate()));
        assertThat(cwcEnrollmentForm.getMeaslesDate(), is(cwcVO.getCWCCareHistoryVO().getMeaslesDate()));
        assertThat(cwcEnrollmentForm.getYfDate(), is(cwcVO.getCWCCareHistoryVO().getYfDate()));
        assertThat(cwcEnrollmentForm.getLastPentaDate(), is(cwcVO.getCWCCareHistoryVO().getLastPentaDate()));
        assertThat(cwcEnrollmentForm.getLastPenta(), is(cwcVO.getCWCCareHistoryVO().getLastPenta()));
        assertThat(cwcEnrollmentForm.getLastOPVDate(), is(cwcVO.getCWCCareHistoryVO().getLastOPVDate()));
        assertThat(cwcEnrollmentForm.getLastOPV(), is(cwcVO.getCWCCareHistoryVO().getLastOPV()));
        assertThat(cwcEnrollmentForm.getLastVitaminA(), is(cwcVO.getCWCCareHistoryVO().getLastVitA()));
        assertThat(cwcEnrollmentForm.getLastMeasles(), is(cwcVO.getCWCCareHistoryVO().getLastMeasles()));
        assertThat(cwcEnrollmentForm.getLastRotavirus(), is(cwcVO.getCWCCareHistoryVO().getLastRotavirus()));
        assertThat(cwcEnrollmentForm.getLastPneumococcal(), is(cwcVO.getCWCCareHistoryVO().getLastPneumococcal()));
        assertThat(cwcEnrollmentForm.getLastIPTiDate(), is(cwcVO.getCWCCareHistoryVO().getLastIPTiDate()));
        assertThat(cwcEnrollmentForm.getLastRotavirusDate(), is(cwcVO.getCWCCareHistoryVO().getLastRotavirusDate()));
        assertThat(cwcEnrollmentForm.getLastPneumococcalDate(), is(cwcVO.getCWCCareHistoryVO().getLastPneumococcalDate()));
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
        cwcEnrollmentForm.setAddHistory(false);
        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(cwcEnrollmentForm.getPatientMotechId())).thenReturn(patient);
        when(patient.dateOfBirth()).thenReturn(DateTime.now());
        when(mockRegisterCWCFormValidator.validatePatient(patient, cwcEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(
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

    @Test
    public void shouldThrowErrorIfHistoryDatesBeforeDOB() throws ObservationNotFoundException {
        ModelMap modelMap = new ModelMap();
        CWCEnrollmentForm cwcEnrollmentForm = createCWCEnrollmentForm();

        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(cwcEnrollmentForm.getPatientMotechId())).thenReturn(patient);
        when(patient.dateOfBirth()).thenReturn(DateTime.now());
        cwcController.save(cwcEnrollmentForm, modelMap);
        assertTrue(modelMap.containsKey("errors"));

        List<String> errorsFromModelMap = (List<String>) modelMap.get("errors");
        Assert.assertThat(errorsFromModelMap, hasItem("lastPentaDate should be after date of birth"));
        Assert.assertThat(errorsFromModelMap, hasItem("lastOPVDate should be after date of birth"));
        Assert.assertThat(errorsFromModelMap, hasItem("bcgDate should be after date of birth"));
        Assert.assertThat(errorsFromModelMap, hasItem("lastRotavirusDate should be after date of birth"));
        verify(mockCareService, never()).enroll(Matchers.<ANCVO>any());
    }

    private CWCEnrollmentForm createCWCEnrollmentForm() {
        final Date registrationDate = DateUtil.newDate(2011, 9, 1).toDate();
        final Date lastBCGDate = DateUtil.newDate(2011, 10, 1).toDate();
        final Date lastVitADate = DateUtil.newDate(2011, 11, 1).toDate();
        final Date lastMeaslesDate = DateUtil.newDate(2011, 9, 2).toDate();
        final Date lastYfDate = DateUtil.newDate(2011, 9, 3).toDate();
        final Date lastPentaDate = DateUtil.newDate(2011, 9, 4).toDate();
        final Date lastOPVDate = DateUtil.newDate(2011, 9, 5).toDate();
        final Date lastIPTiDate = DateUtil.newDate(2011, 9, 6).toDate();
        final Date lastRotavirusDate = DateUtil.newDate(2011, 9, 6).toDate();
        final Date lastPneumococcalDate = DateUtil.newDate(2011, 9, 6).toDate();
        final String staffId = "456";
        final int lastIPTi = 1;
        final int lastPenta = 1;
        final int lastRotavirus = 1;
        final int lastPneumococcal = 1;
        final int lastMeasles = 1;
        final String lastVitaminA = "blue";
        final String patientMotechId = "1234567";
        final int lastOPV = 0;

        final String facilityId = "3232";

        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();
        cwcEnrollmentForm.setStaffId(staffId);
        final FacilityForm facilityForm = new FacilityForm();
        facilityForm.setFacilityId(facilityId);
        cwcEnrollmentForm.setFacilityForm(facilityForm);
        cwcEnrollmentForm.setRegistrationDate(registrationDate);
        cwcEnrollmentForm.setPatientMotechId(patientMotechId);
        cwcEnrollmentForm.setAddHistory(true);
        cwcEnrollmentForm.setBcgDate(lastBCGDate);
        cwcEnrollmentForm.setVitADate(lastVitADate);
        cwcEnrollmentForm.setMeaslesDate(lastMeaslesDate);
        cwcEnrollmentForm.setYfDate(lastYfDate);
        cwcEnrollmentForm.setLastPentaDate(lastPentaDate);
        cwcEnrollmentForm.setLastMeasles(lastMeasles);
        cwcEnrollmentForm.setLastVitaminA(lastVitaminA);
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

        return cwcEnrollmentForm;
    }

}
//
//Expected: a collection containing <FormError{parameter='lastPentaDate', error='should be after date of birth'}>
//        got: <[FormError{parameter='bcgDate', error='should be after date of birth'},
//              FormError{parameter='lastPneumococcalDate', error='should be after date of birth'},
//              FormError{parameter='lastPentaDate', error='should be after date of birth'}, FormError{parameter='yfDate', error='should be after date of birth'}, FormError{parameter='lastRotavirusDate', error='should be after date of birth'}, FormError{parameter='vitADate', error='should be after date of birth'}, FormError{parameter='measlesDate', error='should be after date of birth'}, FormError{parameter='lastOPVDate', error='should be after date of birth'}, FormError{parameter='lastIPTiDate', error='should be after date of birth'}]>
