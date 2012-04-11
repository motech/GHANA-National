package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class RegisterANCFormHandlerTest {

    private RegisterANCFormHandler registerAncFormHandler;
    @Mock
    private CareService careService;
    @Mock
    private MobileMidwifeService mockMobileMidwifeService;
    @Mock
    private FacilityService mockFacilityService;


    @Before
    public void setUp() {
        initMocks(this);
        registerAncFormHandler = new RegisterANCFormHandler();
        ReflectionTestUtils.setField(registerAncFormHandler, "careService", careService);
        ReflectionTestUtils.setField(registerAncFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(registerAncFormHandler, "mobileMidwifeService", mockMobileMidwifeService);
    }

    @Test
    public void shouldSaveANCEnrollmentDetails() {
        final RegisterANCForm registerANCForm = new RegisterANCForm();
        registerANCForm.setAddHistory(true);
        registerANCForm.setAncRegNumber("12432423423");
        registerANCForm.setDate(new Date());
        registerANCForm.setDeliveryDateConfirmed(true);
        registerANCForm.setEstDeliveryDate(new Date(2012, 3, 4));
        registerANCForm.setFacilityId("12345");
        registerANCForm.setGravida(3);
        registerANCForm.setAddCareHistory("IPT_SP TT");
        registerANCForm.setHeight(4.67);
        registerANCForm.setLastIPT("4");
        registerANCForm.setLastIPTDate(new Date(2011, 8, 8));
        registerANCForm.setLastTT("5");
        registerANCForm.setLastTTDate(new Date(2011, 7, 6));
        registerANCForm.setMotechId("343423423");
        registerANCForm.setParity(3);
        registerANCForm.setRegDateToday(RegistrationToday.IN_PAST);
        registerANCForm.setStaffId("2331");
        registerANCForm.setFacilityId("facility");

        Facility facility = mock(Facility.class);
        String mrsFacilityId = "343";

        when(mockFacilityService.getFacilityByMotechId(registerANCForm.getFacilityId())).thenReturn(facility);
        when(facility.getMrsFacilityId()).thenReturn(mrsFacilityId);
        registerAncFormHandler.handleFormEvent(new MotechEvent("form.validation.successful.NurseDataEntry.registerANC", new HashMap<String, Object>() {{
            put("formBean", registerANCForm);
        }}));

        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
        verify(careService).enroll(captor.capture());
        final ANCVO ancVO = captor.getValue();

        assertEquals(registerANCForm.getAddHistory(), ancVO.getAddHistory());
        assertANCCareHistoryDetails(registerANCForm.getANCCareHistories(), registerANCForm.getLastIPT(), registerANCForm.getLastIPTDate(), registerANCForm.getLastTT(), registerANCForm.getLastTTDate(), ancVO.getAncCareHistoryVO());
        assertEquals(registerANCForm.getMotechId(), ancVO.getPatientMotechId());
        assertEquals(registerANCForm.getAncRegNumber(), ancVO.getSerialNumber());
        assertEquals(registerANCForm.getDate(), ancVO.getRegistrationDate());
        assertEquals(registerANCForm.getDeliveryDateConfirmed(), ancVO.getDeliveryDateConfirmed());
        assertEquals(registerANCForm.getEstDeliveryDate(), ancVO.getEstimatedDateOfDelivery());
        assertEquals(registerANCForm.getGravida(), ancVO.getGravida());
        assertEquals(registerANCForm.getHeight(), ancVO.getHeight());
        assertEquals(registerANCForm.getParity(), ancVO.getParity());
        assertEquals(registerANCForm.getRegDateToday(), ancVO.getRegistrationToday());
        assertEquals(registerANCForm.getStaffId(), ancVO.getStaffId());
        assertEquals(mrsFacilityId, ancVO.getFacilityId());
    }

    @Test
    public void shouldUnRegisterForMobileMidWifeIfNotEnrolled() {
        final RegisterANCForm registerANCForm = new RegisterANCForm();

        final Date registartionDate = new Date(2011, 9, 1);
        final String staffId = "456";
        final String patientMotechId = "1234567";
        final String facilityMotechId = "3232";

        registerANCForm.setStaffId(staffId);
        registerANCForm.setFacilityId(facilityMotechId);
        registerANCForm.setDate(registartionDate);
        registerANCForm.setMotechId(patientMotechId);
        registerANCForm.setEnroll(false);

        final String facilityId = "11";
        when(mockFacilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(new Facility(new MRSFacility(facilityId)));

        registerAncFormHandler.handleFormEvent(new MotechEvent("", new HashMap<String, Object>() {{
            put("formBean", registerANCForm);
        }}));

        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
        verify(careService).enroll(captor.capture());
        verify(mockMobileMidwifeService).unRegister(patientMotechId);
        verify(mockMobileMidwifeService, never()).register(Matchers.<MobileMidwifeEnrollment>any());
        final ANCVO ancVO = captor.getValue();

        assertThat(staffId, is(ancVO.getStaffId()));
        assertThat(facilityId, is(facilityId));
        assertThat(registartionDate, is(ancVO.getRegistrationDate()));
        assertThat(patientMotechId, is(ancVO.getPatientMotechId()));

    }
    
    public static void assertANCCareHistoryDetails(List<ANCCareHistory> addCareHistory, String lastIPT, Date lastIPTDate, String lastTT, Date lastTTDate, ANCCareHistoryVO ancCareHistoryVO) {
        assertEquals(addCareHistory, ancCareHistoryVO.getCareHistory());
        assertEquals(lastIPT, ancCareHistoryVO.getLastIPT());
        assertEquals(lastIPTDate, ancCareHistoryVO.getLastIPTDate());
        assertEquals(lastTT, ancCareHistoryVO.getLastTT());
        assertEquals(lastTTDate, ancCareHistoryVO.getLastTTDate());
    }
}
