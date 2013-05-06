package org.motechproject.ghana.national.web;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.validator.FormValidator;
import org.motechproject.ghana.national.validator.RegisterANCFormValidator;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.web.form.ANCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.ANCFormMapper;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;

public class ANCControllerTest {
    private ANCController ancController;
    @Mock
    private FacilityHelper mockFacilityHelper;
    @Mock
    private CareService mockCareService;
    @Mock
    private RegisterANCFormValidator mockValidator;
    @Mock
    private ANCFormMapper mockANCFormMapper;
    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private FormValidator mockFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        ancController = new ANCController();
        ReflectionTestUtils.setField(ancController, "careService", mockCareService);
        ReflectionTestUtils.setField(ancController, "facilityHelper", mockFacilityHelper);
        ReflectionTestUtils.setField(ancController, "registerANCFormValidator", mockValidator);
        ReflectionTestUtils.setField(ancController, "ancFormMapper", mockANCFormMapper);
        ReflectionTestUtils.setField(ancController, "allEncounters", mockAllEncounters);
        ReflectionTestUtils.setField(ancController, "formValidator", mockFormValidator);
    }

    @Test
    public void shouldAddCareHistoryAttributesAndDisplayNewForm() {
        ModelMap modelMap = new ModelMap();
        String motechPatientId = "1212121";
        String ancUrl = ancController.newANC(motechPatientId, modelMap);
        ANCEnrollmentForm ancEnrollmentForm = (ANCEnrollmentForm) modelMap.get("ancEnrollmentForm");
        HashMap<Integer, String> lastIPTValues = new LinkedHashMap<Integer, String>();
        lastIPTValues.put(1, "IPT 1");
        lastIPTValues.put(2, "IPT 2");
        lastIPTValues.put(3, "IPT 3");

        HashMap<Integer, String> lastTTValues = new LinkedHashMap<Integer, String>();
        lastTTValues.put(1, "TT 1");
        lastTTValues.put(2, "TT 2");
        lastTTValues.put(3, "TT 3");
        lastTTValues.put(4, "TT 4");
        lastTTValues.put(5, "TT 5");

        HashMap<Integer, String> lastVitaAValues = new LinkedHashMap<Integer, String>();
        lastVitaAValues.put(1, "Blue");
        lastVitaAValues.put(2, "Red");

        HashMap<Integer, String> lastPneumoValues = new LinkedHashMap<Integer, String>();
        lastPneumoValues.put(1, "Pneumo 1");
        lastPneumoValues.put(2, "Pneumo 2");
        lastPneumoValues.put(3, "Pneumo 3");

        HashMap<Integer, String> lastIronValues = new LinkedHashMap<Integer, String>();
        lastIronValues.put(1,"Yes");
        lastIronValues.put(0,"No");

        HashMap<Integer, String> lastSyphValues = new LinkedHashMap<Integer, String>();
        lastSyphValues.put(1,"Yes");
        lastSyphValues.put(0,"No");

        HashMap<Integer, String> lastMalariaValues = new LinkedHashMap<Integer, String>();
        lastMalariaValues.put(1,"Yes");
        lastMalariaValues.put(0,"No");

        HashMap<Integer, String> lastDiariaValues = new LinkedHashMap<Integer, String>();
        lastDiariaValues.put(1,"Yes");
        lastDiariaValues.put(0,"No");


        assertEquals(ANCController.ENROLL_ANC_URL, ancUrl);
        assertTrue("Form attributes are not equal", reflectionEquals(ancEnrollmentForm, new ANCEnrollmentForm(motechPatientId)));
        assertTrue(reflectionEquals(modelMap.get("careHistories"), Arrays.asList("TT", "IPT_SP", "HEMOGLOBIN", "VITA", "IRON_OR_FOLATE",
                "SYPHILIS", "MALARIA_RAPID_TEST", "DIARRHEA", "PNEUMOCOCCAL_A")));
        assertTrue(reflectionEquals(modelMap.get("lastIPT"), lastIPTValues));
        assertTrue(reflectionEquals(modelMap.get("lastTT"), lastTTValues));
        assertTrue(reflectionEquals(modelMap.get("lastHbLevels"),"14"));
        assertTrue(reflectionEquals(modelMap.get("lastMotherVitaminA"),lastVitaAValues));
        assertTrue(reflectionEquals(modelMap.get("lastIronOrFolate"),lastIronValues));
        assertTrue(reflectionEquals(modelMap.get("lastSyphilis"),lastSyphValues));
        assertTrue(reflectionEquals(modelMap.get("lastMalaria"),lastMalariaValues));
        assertTrue(reflectionEquals(modelMap.get("lastDiarrhea"),lastDiariaValues));
        assertTrue(reflectionEquals(modelMap.get("lastPnuemonia"),lastPneumoValues));

        verify(mockFacilityHelper).locationMap();
    }

    @Test
    public void shouldSaveANCEnrollment() throws ObservationNotFoundException {
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();
        ancEnrollmentForm.setAddHistory(false);
        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(ancEnrollmentForm.getMotechPatientId())).thenReturn(patient);
        when(mockValidator.validatePatient(patient, ancEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(Collections.<FormError>emptyList());
        when(mockFormValidator.validateIfStaffExists(ancEnrollmentForm.getStaffId())).thenReturn(Collections.<FormError>emptyList());
        when(patient.dateOfBirth()).thenReturn(DateTime.now());
        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);

        ancController.save(ancEnrollmentForm, modelMap);
        verify(mockCareService).enroll(captor.capture());
        final ANCVO ancVO = captor.getValue();

        compareANCEnrollmentFormWithANCVO(ancEnrollmentForm, ancVO);
        assertTrue(modelMap.containsKey("success"));
        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
    }

    @Test
    public void shouldNotSaveANCEnrollmentDuringValidationErrors() throws ObservationNotFoundException {
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();
        ArrayList<FormError> patientErrors = new ArrayList<FormError>();
        FormError motechIdError = new FormError("motechId", "MotechId NOT FOUND");
        patientErrors.add(motechIdError);

        ArrayList<FormError> staffErrors = new ArrayList<FormError>();
        FormError staffIdError = new FormError("staffId", "staffId not found");
        staffErrors.add(staffIdError);
        when(mockFormValidator.validateIfStaffExists(ancEnrollmentForm.getStaffId())).thenReturn(staffErrors);
        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(ancEnrollmentForm.getMotechPatientId())).thenReturn(patient);
        when(mockValidator.validatePatient(patient, ancEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(patientErrors);
        when(patient.dateOfBirth()).thenReturn(DateTime.now());
        ancController.save(ancEnrollmentForm, modelMap);
        verify(mockCareService, never()).enroll((ANCVO) null);
        assertTrue(modelMap.containsKey("validationErrors"));

        ArrayList<FormError> errorsFromModelMap = (ArrayList<FormError>) modelMap.get("validationErrors");
        assertTrue(errorsFromModelMap.contains(motechIdError));
        assertTrue(errorsFromModelMap.contains(staffIdError));
        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
    }

    @Test
    public void shouldPopulateEncounterInfoIfEncounterExists() {
        String motechPatientId = "1";
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();
        ArrayList<FormError> errors = new ArrayList<FormError>();

        MRSEncounter mrsEncounter = new MRSEncounter();
        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(ancEnrollmentForm.getMotechPatientId())).thenReturn(patient);
        when(mockValidator.validatePatient(patient, ancEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(errors);
        when(mockAllEncounters.getLatest(motechPatientId, ANC_REG_VISIT.value())).thenReturn(mrsEncounter);
        when(mockANCFormMapper.convertMRSEncounterToView(mrsEncounter)).thenReturn(ancEnrollmentForm);

        ancController.newANC(motechPatientId, modelMap);
        assertTrue(modelMap.containsKey("ancEnrollmentForm"));
        assertTrue(reflectionEquals(modelMap.get("ancEnrollmentForm"), ancEnrollmentForm));
        verify(mockAllEncounters).getLatest(eq(motechPatientId), anyString());
        verify(mockANCFormMapper).populatePregnancyInfo(Matchers.<String>any(), eq(ancEnrollmentForm));
        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
    }

    @Test
    public void shouldThrowErrorsWhenThePatientIsNotFoundAndNotFemaleWhileRenderingANCPage() {
        String motechPatientId = "1";
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = new ANCEnrollmentForm(motechPatientId);
        ArrayList<FormError> errors = new ArrayList<FormError>() {{
            add(new FormError("motechId", "should be female for registering into ANC"));
        }};
        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(ancEnrollmentForm.getMotechPatientId())).thenReturn(patient);
        when(mockValidator.validatePatient(patient, ancEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(errors);
        ancController.newANC(motechPatientId, modelMap);
        assertTrue(modelMap.containsKey("ancEnrollmentForm"));

        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
    }

    @Test
    public void shouldThrowErrorIfHistoryDatesBeforeDOB() throws ObservationNotFoundException {
        ModelMap modelMap = new ModelMap();
        List<FormError> datesError = new ArrayList<FormError>(){{
            add(new FormError("lastTTDate","should be after date of birth"));
            add(new FormError("lastIPTDate", "should be after date of birth"));
        }};
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();

        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(ancEnrollmentForm.getMotechPatientId())).thenReturn(patient);
        when(mockValidator.validatePatient(patient, ancEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList())).thenReturn(datesError);

        ancController.save(ancEnrollmentForm,modelMap);
        assertTrue(modelMap.containsKey("validationErrors"));

        List<FormError> errorsFromModelMap = (List<FormError>) modelMap.get("validationErrors");
        assertThat(errorsFromModelMap,hasItem(datesError.get(0)));
        assertThat(errorsFromModelMap,hasItem(datesError.get(1)));
        verify(mockCareService,never()).enroll(Matchers.<ANCVO>any());
    }

    private void checkIfCareHistoryAttributesArePlacedInModelMap(ModelMap modelMap, ANCEnrollmentForm ancEnrollmentForm) {
        assertTrue(modelMap.containsKey("ancEnrollmentForm"));
        assertTrue(modelMap.containsKey("careHistories"));
        assertTrue(modelMap.containsKey("lastTT"));
        assertTrue(modelMap.containsKey("lastIPT"));
        assertTrue(reflectionEquals(ancEnrollmentForm, modelMap.get("ancEnrollmentForm")));
    }

    private void compareANCEnrollmentFormWithANCVO(ANCEnrollmentForm ancEnrollmentForm, ANCVO ancVO) {
        assertEquals(ancEnrollmentForm.getAddHistory(), ancVO.getAddHistory());
        assertEquals(ancEnrollmentForm.getSerialNumber(), ancVO.getSerialNumber());
        assertEquals(ancEnrollmentForm.getRegistrationDate(), ancVO.getRegistrationDate());
        assertEquals(ancEnrollmentForm.getDeliveryDateConfirmed(), ancVO.getDeliveryDateConfirmed());
        assertEquals(ancEnrollmentForm.getEstimatedDateOfDelivery(), ancVO.getEstimatedDateOfDelivery());
        assertEquals(ancEnrollmentForm.getFacilityForm().getFacilityId(), ancVO.getFacilityId());
        assertEquals(ancEnrollmentForm.getGravida(), ancVO.getGravida());
        assertEquals(ancEnrollmentForm.getHeight(), ancVO.getHeight());
        assertEquals(ancEnrollmentForm.getLastIPT(), ancVO.getAncCareHistoryVO().getLastIPT());
        assertEquals(ancEnrollmentForm.getLastIPTDate(), ancVO.getAncCareHistoryVO().getLastIPTDate());
        assertEquals(ancEnrollmentForm.getLastTT(), ancVO.getAncCareHistoryVO().getLastTT());
        assertEquals(ancEnrollmentForm.getLastTTDate(), ancVO.getAncCareHistoryVO().getLastTTDate());

        assertEquals(ancEnrollmentForm.getLastHbLevels(), ancVO.getAncCareHistoryVO().getLastHbLevels());
        assertEquals(ancEnrollmentForm.getLastHbDate(), ancVO.getAncCareHistoryVO().getLastHbDate());
        assertEquals(ancEnrollmentForm.getLastMotherVitaminA(), ancVO.getAncCareHistoryVO().getLastMotherVitaminA());
        assertEquals(ancEnrollmentForm.getLastMotherVitaminADate(), ancVO.getAncCareHistoryVO().getLastMotherVitaminADate());
        assertEquals(ancEnrollmentForm.getLastIronOrFolate(), ancVO.getAncCareHistoryVO().getLastIronOrFolate());
        assertEquals(ancEnrollmentForm.getLastIronOrFolateDate(), ancVO.getAncCareHistoryVO().getLastIronOrFolateDate());
        assertEquals(ancEnrollmentForm.getLastSyphilis(), ancVO.getAncCareHistoryVO().getLastSyphilis());
        assertEquals(ancEnrollmentForm.getLastSyphilisDate(), ancVO.getAncCareHistoryVO().getLastSyphilisDate());
        assertEquals(ancEnrollmentForm.getLastMalaria(), ancVO.getAncCareHistoryVO().getLastMalaria());
        assertEquals(ancEnrollmentForm.getLastMalariaDate(), ancVO.getAncCareHistoryVO().getLastMalariaDate());
        assertEquals(ancEnrollmentForm.getLastDiarrhea(), ancVO.getAncCareHistoryVO().getLastDiarrhea());
        assertEquals(ancEnrollmentForm.getLastDiarrheaDate(), ancVO.getAncCareHistoryVO().getLastDiarrheaDate());
        assertEquals(ancEnrollmentForm.getLastPnuemonia(), ancVO.getAncCareHistoryVO().getLastPnuemonia());
        assertEquals(ancEnrollmentForm.getLastPnuemoniaDate(), ancVO.getAncCareHistoryVO().getLastPnuemoniaDate());

        assertEquals(ancEnrollmentForm.getMotechPatientId(), ancVO.getPatientMotechId());
        assertEquals(ancEnrollmentForm.getParity(), ancVO.getParity());
        assertEquals(ancEnrollmentForm.getRegistrationToday(), ancVO.getRegistrationToday());
        assertEquals(ancEnrollmentForm.getStaffId(), ancVO.getStaffId());
    }

    private ANCEnrollmentForm createTestANCEnrollmentForm() {
        ANCEnrollmentForm ancEnrollmentForm = new ANCEnrollmentForm();
        ancEnrollmentForm.setAddHistory(true);
        ancEnrollmentForm.setSerialNumber("12432423423");
        ancEnrollmentForm.setRegistrationDate(new Date());
        ancEnrollmentForm.setDeliveryDateConfirmed(true);
        ancEnrollmentForm.setEstimatedDateOfDelivery(DateUtil.newDate(2012, 3, 4).toDate());
        FacilityForm facilityForm = new FacilityForm();
        facilityForm.setFacilityId("21212");
        ancEnrollmentForm.setFacilityForm(facilityForm);
        ancEnrollmentForm.setGravida(3);
        ancEnrollmentForm.setHeight(4.67);
        ancEnrollmentForm.setLastIPT("4");
        ancEnrollmentForm.setLastIPTDate(DateUtil.newDate(2011, 8, 8).toDate());
        ancEnrollmentForm.setLastTT("5");
        ancEnrollmentForm.setLastTTDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastHbLevels("14");
        ancEnrollmentForm.setLastHbDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastMotherVitaminA("1");
        ancEnrollmentForm.setLastMotherVitaminADate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastIronOrFolate("1");
        ancEnrollmentForm.setLastIronOrFolateDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastSyphilis("1");
        ancEnrollmentForm.setLastSyphilisDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastMalaria("1");
        ancEnrollmentForm.setLastMalariaDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastDiarrhea("1");
        ancEnrollmentForm.setLastDiarrheaDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setLastPnuemonia("1");
        ancEnrollmentForm.setLastPnuemoniaDate(DateUtil.newDate(2011, 7, 6).toDate());
        ancEnrollmentForm.setMotechPatientId("343423423");
        ancEnrollmentForm.setParity(3);
        ancEnrollmentForm.setRegistrationToday(RegistrationToday.IN_PAST);
        ancEnrollmentForm.setStaffId("2331");
        return ancEnrollmentForm;
    }
}
