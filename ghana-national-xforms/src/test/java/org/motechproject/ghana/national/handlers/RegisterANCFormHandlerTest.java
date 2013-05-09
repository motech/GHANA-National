package org.motechproject.ghana.national.handlers;

import org.hamcrest.CoreMatchers;
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
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
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
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(careService).enroll(Matchers.<CwcVO>any());
        try {
            registerAncFormHandler.handleFormEvent(new RegisterANCForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), CoreMatchers.is("Encountered exception while processing register ANC form"));
        }
    }

    @Test
    public void shouldGetCurrentDateIfRegistrationDateisNull() throws ObservationNotFoundException {
        final RegisterANCForm ancForm = new RegisterANCForm();
        Facility facility = mock(Facility.class);
        String mrsFacilityId = "343";
        when(facility.getMrsFacilityId()).thenReturn(mrsFacilityId);
        when(mockFacilityService.getFacilityByMotechId(ancForm.getFacilityId())).thenReturn(facility);

        registerAncFormHandler.handleFormEvent(ancForm);

        ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
        verify(careService).enroll(captor.capture());
        assertThat(captor.getValue().getRegistrationDate(), is(DateUtil.today().toDate()));
    }

    @Test
    public void shouldSaveANCEnrollmentDetails() throws ObservationNotFoundException {
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

        registerANCForm.setLastHbLevels("14");
        registerANCForm.setLastHbDate(new Date(2011, 7, 6));
        registerANCForm.setLastMotherVitaminA("1");
        registerANCForm.setLastMotherVitaminADate(new Date(2011, 7, 6));
        registerANCForm.setLastIronOrFolate("1");
        registerANCForm.setLastIronOrFolateDate(new Date(2011, 7, 6));
        registerANCForm.setLastSyphilis("1");
        registerANCForm.setLastSyphilisDate(new Date(2011, 7, 6));
        registerANCForm.setLastMalaria("1");
        registerANCForm.setLastMalariaDate(new Date(2011, 7, 6));
        registerANCForm.setLastDiarrhea("1");
        registerANCForm.setLastDiarrheaDate(new Date(2011, 7, 6));
        registerANCForm.setLastPnuemonia("0");
        registerANCForm.setLastPnuemoniaDate(new Date(2011, 7, 6));

        registerANCForm.setMotechId("343423423");
        registerANCForm.setParity(3);
        registerANCForm.setRegDateToday(RegistrationToday.IN_PAST);
        registerANCForm.setStaffId("2331");
        registerANCForm.setFacilityId("facility");

        Facility facility = mock(Facility.class);
        String mrsFacilityId = "343";
        when(facility.getMrsFacilityId()).thenReturn(mrsFacilityId);
        when(mockFacilityService.getFacilityByMotechId(registerANCForm.getFacilityId())).thenReturn(facility);
        registerAncFormHandler.handleFormEvent(registerANCForm);

        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
        verify(careService).enroll(captor.capture());
        final ANCVO ancVO = captor.getValue();

        assertEquals(registerANCForm.getAddHistory(), ancVO.getAddHistory());
        assertANCCareHistoryDetails(registerANCForm.getANCCareHistories(), registerANCForm.getLastIPT(), registerANCForm.getLastIPTDate(), registerANCForm.getLastTT(), registerANCForm.getLastTTDate(),
                registerANCForm.getLastHbLevels(), registerANCForm.getLastHbDate(), registerANCForm.getLastMotherVitaminA(), registerANCForm.getLastMotherVitaminADate(),
                registerANCForm.getLastIronOrFolate(), registerANCForm.getLastIronOrFolateDate(), registerANCForm.getLastSyphilis(), registerANCForm.getLastSyphilisDate(),
                registerANCForm.getLastMalaria(), registerANCForm.getLastMalariaDate(), registerANCForm.getLastDiarrhea(), registerANCForm.getLastDiarrheaDate(),
                registerANCForm.getLastPnuemonia(), registerANCForm.getLastPnuemoniaDate(), ancVO.getAncCareHistoryVO());
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

    public static void assertANCCareHistoryDetails(List<ANCCareHistory> addCareHistory, String lastIPT, Date lastIPTDate,  String lastTT, Date lastTTDate, String lastHBLevels, Date lastHbDate,
                                                  String lastMotherVitaA, Date lastMotherVitaADate, String lastIron, Date lastIronDate, String lastSyphilis, Date lastSyphDate, String lastMalaria, Date lastMalariaDate,
                                                  String lastDiarhea, Date lastDiarheaDate, String lastPneumonia, Date lastPneumoDate, ANCCareHistoryVO ancCareHistoryVO) {
        assertEquals(addCareHistory, ancCareHistoryVO.getCareHistory());
        assertEquals(lastIPT, ancCareHistoryVO.getLastIPT());
        assertEquals(lastIPTDate, ancCareHistoryVO.getLastIPTDate());
        assertEquals(lastTT, ancCareHistoryVO.getLastTT());
        assertEquals(lastTTDate, ancCareHistoryVO.getLastTTDate());
        assertEquals(lastHBLevels, ancCareHistoryVO.getLastHbLevels());
        assertEquals(lastHbDate, ancCareHistoryVO.getLastHbDate());
        assertEquals(lastMotherVitaA, ancCareHistoryVO.getLastMotherVitaminA());
        assertEquals(lastMotherVitaADate, ancCareHistoryVO.getLastMotherVitaminADate());
        assertEquals(lastIron, ancCareHistoryVO.getLastIronOrFolate());
        assertEquals(lastIronDate, ancCareHistoryVO.getLastIronOrFolateDate());
        assertEquals(lastSyphilis, ancCareHistoryVO.getLastSyphilis());
        assertEquals(lastSyphDate, ancCareHistoryVO.getLastSyphilisDate());
        assertEquals(lastMalaria, ancCareHistoryVO.getLastMalaria());
        assertEquals(lastMalariaDate, ancCareHistoryVO.getLastMalariaDate());
        assertEquals(lastDiarhea, ancCareHistoryVO.getLastDiarrhea());
        assertEquals(lastDiarheaDate, ancCareHistoryVO.getLastDiarrheaDate());
        assertEquals(lastPneumonia, ancCareHistoryVO.getLastPnuemonia());
        assertEquals(lastPneumoDate, ancCareHistoryVO.getLastPnuemoniaDate());

    }
}
