package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class ANCRegistrationFormHandlerTest {

    private ANCRegistrationFormHandler ancRegistrationFormHandler;
    @Mock
    private CareService careService;
    @Mock
    private MobileMidwifeService mockMobileMidwifeService;
    @Mock
    private FacilityService mockFacilityService;


    @Before
    public void setUp() {
        initMocks(this);
        ancRegistrationFormHandler = new ANCRegistrationFormHandler();
        ReflectionTestUtils.setField(ancRegistrationFormHandler, "careService", careService);
        ReflectionTestUtils.setField(ancRegistrationFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(ancRegistrationFormHandler, "mobileMidwifeService", mockMobileMidwifeService);
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
        registerANCForm.setHeight(4.67);
        registerANCForm.setLastIPT("4");
        registerANCForm.setLastIPTDate(new Date(2011, 8, 8));
        registerANCForm.setLastTT("5");
        registerANCForm.setLastTTDate(new Date(2011, 7, 6));
        registerANCForm.setMotechId("343423423");
        registerANCForm.setParity(3);
        registerANCForm.setRegDateToday(RegistrationToday.IN_PAST);
        registerANCForm.setRegPhone("045353453434");
        registerANCForm.setStaffId("2331");
        registerANCForm.setFacilityId("facility");
        setMobileMidwifeEnrollment(registerANCForm);

        Facility facility = mock(Facility.class);
        String mrsFacilityId = "343";

        when(mockFacilityService.getFacilityByMotechId(registerANCForm.getFacilityId())).thenReturn(facility);
        when(facility.getMrsFacilityId()).thenReturn(mrsFacilityId);
        ancRegistrationFormHandler.handleFormEvent(new MotechEvent("form.validation.successful.NurseDataEntry.registerANC", new HashMap<String, Object>() {{
            put("formBean", registerANCForm);
        }}));

        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
        final ArgumentCaptor<MobileMidwifeEnrollment> mobileMidwifeEnrollmentCaptor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(careService).enroll(captor.capture());
        verify(mockMobileMidwifeService).createOrUpdateEnrollment(mobileMidwifeEnrollmentCaptor.capture());
        final ANCVO ancVO = captor.getValue();

        assertEquals(registerANCForm.getAddHistory(), ancVO.getAddHistory());
        assertEquals(registerANCForm.getAncRegNumber(), ancVO.getSerialNumber());
        assertEquals(registerANCForm.getDate(), ancVO.getRegistrationDate());
        assertEquals(registerANCForm.getDeliveryDateConfirmed(), ancVO.getDeliveryDateConfirmed());
        assertEquals(registerANCForm.getEstDeliveryDate(), ancVO.getEstimatedDateOfDelivery());
        assertEquals(registerANCForm.getGravida(), ancVO.getGravida());
        assertEquals(registerANCForm.getHeight(), ancVO.getHeight());
        assertEquals(registerANCForm.getLastIPT(), ancVO.getAncCareHistoryVO().getLastIPT());
        assertEquals(registerANCForm.getLastIPTDate(), ancVO.getAncCareHistoryVO().getLastIPTDate());
        assertEquals(registerANCForm.getLastTT(), ancVO.getAncCareHistoryVO().getLastTT());
        assertEquals(registerANCForm.getLastTTDate(), ancVO.getAncCareHistoryVO().getLastTTDate());
        assertEquals(registerANCForm.getMotechId(), ancVO.getPatientMotechId());
        assertEquals(registerANCForm.getParity(), ancVO.getParity());
        assertEquals(registerANCForm.getRegDateToday(), ancVO.getRegistrationToday());
        assertEquals(registerANCForm.getStaffId(), ancVO.getStaffId());
        assertEquals(mrsFacilityId, ancVO.getFacilityId());

        assertMobileMidwifeFormEnrollment(registerANCForm, mobileMidwifeEnrollmentCaptor.getValue());
    }

    private void assertMobileMidwifeFormEnrollment(RegisterANCForm exptectedForm, MobileMidwifeEnrollment actual) {

        if(exptectedForm.isEnrolledForProgram()) {
            assertNotNull(exptectedForm.getConsent());
        }
        assertThat(actual.getConsent(), is(exptectedForm.getConsent()));
        assertThat(actual.getStaffId(), is(exptectedForm.getStaffId()));
        assertThat(actual.getFacilityId(), is(exptectedForm.getFacilityId()));
        assertThat(actual.getPatientId(), is(exptectedForm.getMotechId()));
        assertThat(actual.getServiceType(), is(exptectedForm.getServiceType()));
        assertThat(actual.getReasonToJoin(), is(exptectedForm.getReasonToJoin()));
        assertThat(actual.getMedium(), is(exptectedForm.getMediumStripingOwnership()));
        assertThat(actual.getDayOfWeek(), is(exptectedForm.getDayOfWeek()));
        assertThat(actual.getTimeOfDay(), is(exptectedForm.getTimeOfDay()));
        assertThat(actual.getLanguage(), is(exptectedForm.getLanguage()));
        assertThat(actual.getLearnedFrom(), is(exptectedForm.getLearnedFrom()));
        assertThat(actual.getPhoneNumber(), is(exptectedForm.getPhoneNumber()));
        assertThat(actual.getPhoneOwnership(), is(exptectedForm.getPhoneOwnership()));
    }

    private void setMobileMidwifeEnrollment(RegisterANCForm registerANCForm) {
        new MobileMidwifeBuilder().enroll(true)
                .consent(true).dayOfWeek(DayOfWeek.Monday).language(Language.EN).learnedFrom(LearnedFrom.FRIEND).format("PERS_VOICE")
                .timeOfDay(new Time(10, 02)).messageStartWeek("10").phoneNumber("9500012343")
                .phoneOwnership(PhoneOwnership.PERSONAL).reasonToJoin(ReasonToJoin.FAMILY_FRIEND_DELIVERED)
                .serviceType(ServiceType.CHILD_CARE)
                .buildRegisterANCForm(registerANCForm);

    }

}
