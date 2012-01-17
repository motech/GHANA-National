package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.service.ANCService;
import org.motechproject.ghana.national.validator.RegisterANCFormValidator;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.web.form.ANCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.ANCFormMapper;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSEncounter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCControllerTest {
    private ANCController ancController;
    @Mock
    private FacilityHelper mockFacilityHelper;
    @Mock
    private ANCService mockANCService;
    @Mock
    private RegisterANCFormValidator mockValidator;
    @Mock
    private ANCFormMapper mockANCFormMapper;

    @Before
    public void setUp() {
        initMocks(this);
        ancController = new ANCController();
        ReflectionTestUtils.setField(ancController, "ancService", mockANCService);
        ReflectionTestUtils.setField(ancController, "facilityHelper", mockFacilityHelper);
        ReflectionTestUtils.setField(ancController, "registerANCFormValidator", mockValidator);
        ReflectionTestUtils.setField(ancController, "ancFormMapper", mockANCFormMapper);
    }

    @Test
    public void shouldAddCareHistoryAttributesAndDisplayNewForm() {
        ModelMap modelMap = new ModelMap();
        String motechPatientId = "1212121";
        String ancUrl = ancController.newANC(motechPatientId, modelMap);
        ANCEnrollmentForm ancEnrollmentForm = (ANCEnrollmentForm) modelMap.get("ancEnrollmentForm");

        assertEquals(ANCController.ENROLL_ANC_URL, ancUrl);
        assertTrue("Form attributes are not equal", reflectionEquals(ancEnrollmentForm, new ANCEnrollmentForm(motechPatientId)));
        assertTrue(reflectionEquals(modelMap.get("careHistories"), Arrays.asList("TT", "IPT")));
        assertTrue(reflectionEquals(modelMap.get("lastIPT"), Arrays.asList("IPT 1", "IPT 2", "IPT 3")));
        assertTrue(reflectionEquals(modelMap.get("lastTT"), Arrays.asList("TT 1", "TT 2", "TT 3", "TT 4", "TT 5")));
        verify(mockFacilityHelper).locationMap();
    }

    @Test
    public void shouldSaveANCEnrollment() {
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();
        when(mockValidator.validatePatientFacilityStaff(ancEnrollmentForm.getMotechPatientId(), ancEnrollmentForm.getFacilityForm().getFacilityId(), ancEnrollmentForm.getStaffId())).thenReturn(Arrays.<FormError>asList());
        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);

        ancController.save(ancEnrollmentForm, modelMap);
        verify(mockANCService).enroll(captor.capture());
        final ANCVO ancVO = captor.getValue();

        compareANCEnrollmentFormWithANCVO(ancEnrollmentForm, ancVO);
        assertTrue(modelMap.containsKey("success"));
        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
    }

    @Test
    public void shouldNotSaveANCEnrollmentDuringValidationErrors() {
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();
        ArrayList<FormError> errors = new ArrayList<FormError>();
        FormError motechIdError = new FormError("motechId", "MotechId NOT FOUND");
        errors.add(motechIdError);
        FormError staffIdError = new FormError("staffId", "staffId not found");
        errors.add(staffIdError);

        when(mockValidator.validatePatientFacilityStaff(ancEnrollmentForm.getMotechPatientId(), ancEnrollmentForm.getFacilityForm().getFacilityId(), ancEnrollmentForm.getStaffId())).thenReturn(errors);

        ancController.save(ancEnrollmentForm, modelMap);
        verify(mockANCService, never()).enroll(null);
        assertTrue(modelMap.containsKey("validationErrors"));

        ArrayList<FormError> errorsFromModelMap = (ArrayList<FormError>) modelMap.get("validationErrors");
        assertEquals(2, errorsFromModelMap.size());
        assertTrue(errorsFromModelMap.contains(motechIdError));
        assertTrue(errorsFromModelMap.contains(staffIdError));
        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
    }

    @Test
    public void shouldPopulateEncounterInfoIfEncounterExists(){
        String motechPatientId = "1";
        ModelMap modelMap = new ModelMap();
        ANCEnrollmentForm ancEnrollmentForm = createTestANCEnrollmentForm();
        ArrayList<FormError> errors = new ArrayList<FormError>();

        MRSEncounter mrsEncounter = new MRSEncounter();

        when(mockValidator.validatePatient(motechPatientId)).thenReturn(errors);
        when(mockANCService.getEncounter(motechPatientId)).thenReturn(mrsEncounter);
        when(mockANCFormMapper.convertMRSEncounterToView(mrsEncounter)).thenReturn(ancEnrollmentForm);

        ancController.newANC(motechPatientId, modelMap);
        assertTrue(modelMap.containsKey("ancEnrollmentForm"));
        assertTrue(reflectionEquals(modelMap.get("ancEnrollmentForm"),ancEnrollmentForm));

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
        when(mockValidator.validatePatient(motechPatientId)).thenReturn(errors);
        ancController.newANC(motechPatientId, modelMap);
        assertTrue(modelMap.containsKey("ancEnrollmentForm"));

        checkIfCareHistoryAttributesArePlacedInModelMap(modelMap, ancEnrollmentForm);
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
        assertEquals(ancEnrollmentForm.getLastIPT(), ancVO.getLastIPT());
        assertEquals(ancEnrollmentForm.getLastIPTDate(), ancVO.getLastIPTDate());
        assertEquals(ancEnrollmentForm.getLastTT(), ancVO.getLastTT());
        assertEquals(ancEnrollmentForm.getLastTTDate(), ancVO.getLastTTDate());
        assertEquals(ancEnrollmentForm.getMotechPatientId(), ancVO.getMotechPatientId());
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
        ancEnrollmentForm.setEstimatedDateOfDelivery(new Date(2012, 3, 4));
        FacilityForm facilityForm = new FacilityForm();
        facilityForm.setFacilityId("21212");
        ancEnrollmentForm.setFacilityForm(facilityForm);
        ancEnrollmentForm.setGravida(3);
        ancEnrollmentForm.setHeight(4.67);
        ancEnrollmentForm.setLastIPT("4");
        ancEnrollmentForm.setLastIPTDate(new Date(2011, 8, 8));
        ancEnrollmentForm.setLastTT("5");
        ancEnrollmentForm.setLastTTDate(new Date(2011, 7, 6));
        ancEnrollmentForm.setMotechPatientId("343423423");
        ancEnrollmentForm.setParity(3);
        ancEnrollmentForm.setRegistrationToday(RegistrationToday.IN_PAST);
        ancEnrollmentForm.setStaffId("2331");
        return ancEnrollmentForm;
    }


}
