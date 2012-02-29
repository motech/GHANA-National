package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSFacility;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterCWCFormHandlerTest {

    @Mock
    private CareService careService;
    @Mock
    private MobileMidwifeService mobileMidwifeService;

    @Mock
    private FacilityService mockFacilityService;

    RegisterCWCFormHandler formHandlerRegister;

    @Before
    public void setUp() {
        initMocks(this);
        formHandlerRegister = new RegisterCWCFormHandler();
        ReflectionTestUtils.setField(formHandlerRegister, "careService", careService);
        ReflectionTestUtils.setField(formHandlerRegister, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(formHandlerRegister, "mobileMidwifeService", mobileMidwifeService);
    }


    @Test
    public void shouldSaveCWCEnrollmentForm() {
        final RegisterCWCForm registerCWCForm = new RegisterCWCForm();

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
        final String facilityMotechId = "3232";

        registerCWCForm.setStaffId(staffId);
        registerCWCForm.setFacilityId(facilityMotechId);
        registerCWCForm.setRegistrationDate(registartionDate);
        registerCWCForm.setMotechId(patientMotechId);
        registerCWCForm.setBcgDate(lastBCGDate);
        registerCWCForm.setLastVitaminADate(lastVitADate);
        registerCWCForm.setMeaslesDate(lastMeaslesDate);
        registerCWCForm.setYellowFeverDate(lastYfDate);
        registerCWCForm.setLastPentaDate(lastPentaDate);
        registerCWCForm.setLastPenta(lastPenta);
        registerCWCForm.setLastOPVDate(lastOPVDate);
        registerCWCForm.setLastOPV(lastOPV);
        registerCWCForm.setLastIPTiDate(lastIPTiDate);
        registerCWCForm.setLastIPTi(lastIPTi);
        registerCWCForm.setAddCareHistory("VITA_A IPTI BCG OPV PENTA MEASLES YF");
        setMobileMidwifeEnrollment(registerCWCForm);

        final String facilityId = "11";
        when(mockFacilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(new Facility(new MRSFacility(facilityId)));

        formHandlerRegister.handleFormEvent(new MotechEvent("", new HashMap<String, Object>() {{
            put("formBean", registerCWCForm);
        }}));

        final ArgumentCaptor<CwcVO> captor = ArgumentCaptor.forClass(CwcVO.class);
        final ArgumentCaptor<MobileMidwifeEnrollment> mobileMidwifeEnrollmentCaptor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(careService).enroll(captor.capture());
        verify(mobileMidwifeService).register(mobileMidwifeEnrollmentCaptor.capture());
        final CwcVO cwcVO = captor.getValue();

        assertThat(staffId, is(cwcVO.getStaffId()));
        assertThat(facilityId, is(facilityId));
        assertThat(registartionDate, is(cwcVO.getRegistrationDate()));
        assertThat(patientMotechId, is(cwcVO.getPatientMotechId()));
        assertCwcCareHistoryDetails(registerCWCForm.getCWCCareHistories(), lastBCGDate, lastVitADate, lastMeaslesDate, lastYfDate, lastPentaDate, lastOPVDate, lastIPTiDate, lastPenta, lastOPV, cwcVO.getCWCCareHistoryVO());

        assertMobileMidwifeFormEnrollment(registerCWCForm, mobileMidwifeEnrollmentCaptor.getValue());
    }

    @Test
    public void shouldUnRegisterForMobileMidWifeIfNotEnrolled() {
        final RegisterCWCForm registerCWCForm = new RegisterCWCForm();

        final Date registartionDate = new Date(2011, 9, 1);
        final String staffId = "456";
        final String patientMotechId = "1234567";
        final String facilityMotechId = "3232";

        registerCWCForm.setStaffId(staffId);
        registerCWCForm.setFacilityId(facilityMotechId);
        registerCWCForm.setRegistrationDate(registartionDate);
        registerCWCForm.setMotechId(patientMotechId);
        registerCWCForm.setEnroll(false);

        final String facilityId = "11";
        when(mockFacilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(new Facility(new MRSFacility(facilityId)));

        formHandlerRegister.handleFormEvent(new MotechEvent("", new HashMap<String, Object>() {{
            put("formBean", registerCWCForm);
        }}));

        final ArgumentCaptor<CwcVO> captor = ArgumentCaptor.forClass(CwcVO.class);
        verify(careService).enroll(captor.capture());
        verify(mobileMidwifeService).unRegister(patientMotechId);
        verify(mobileMidwifeService, never()).register(Matchers.<MobileMidwifeEnrollment>any());
        final CwcVO cwcVO = captor.getValue();

        assertThat(staffId, is(cwcVO.getStaffId()));
        assertThat(facilityId, is(facilityId));
        assertThat(registartionDate, is(cwcVO.getRegistrationDate()));
        assertThat(patientMotechId, is(cwcVO.getPatientMotechId()));

    }

    public static void assertCwcCareHistoryDetails(List<CwcCareHistory> cwcCareHistories, Date lastBCGDate, Date lastVitADate, Date lastMeaslesDate, Date lastYfDate, Date lastPentaDate, Date lastOPVDate, Date lastIPTiDate, int lastPenta, int lastOPV, CWCCareHistoryVO cwcCareHistoryVO) {
        assertThat(cwcCareHistories, is(cwcCareHistoryVO.getCwcCareHistories()));
        assertThat(lastBCGDate, is(cwcCareHistoryVO.getBcgDate()));
        assertThat(lastVitADate, is(cwcCareHistoryVO.getVitADate()));
        assertThat(lastMeaslesDate, is(cwcCareHistoryVO.getMeaslesDate()));
        assertThat(lastYfDate, is(cwcCareHistoryVO.getYfDate()));
        assertThat(lastPentaDate, is(cwcCareHistoryVO.getLastPentaDate()));
        assertThat(lastPenta, is(cwcCareHistoryVO.getLastPenta()));
        assertThat(lastOPVDate, is(cwcCareHistoryVO.getLastOPVDate()));
        assertThat(lastOPV, is(cwcCareHistoryVO.getLastOPV()));
        assertThat(lastIPTiDate, is(cwcCareHistoryVO.getLastIPTiDate()));
    }

    private void assertMobileMidwifeFormEnrollment(RegisterCWCForm exptectedForm, MobileMidwifeEnrollment actual) {

        if (exptectedForm.isEnrolledForMobileMidwifeProgram()) {
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
        assertThat(actual.getPhoneNumber(), is(exptectedForm.getMmRegPhone()));
        assertThat(actual.getPhoneOwnership(), is(exptectedForm.getPhoneOwnership()));
    }

    private void setMobileMidwifeEnrollment(RegisterCWCForm registerCWCForm) {
        new MobileMidwifeBuilder()
                .enroll(true)
                .consent(true).dayOfWeek(DayOfWeek.Monday).language(Language.EN).learnedFrom(LearnedFrom.FRIEND).format("PERS_VOICE")
                .timeOfDay(new Time(10, 02)).messageStartWeek("10").phoneNumber("9500012343")
                .phoneOwnership(PhoneOwnership.PERSONAL).reasonToJoin(ReasonToJoin.FAMILY_FRIEND_DELIVERED)
                .serviceType(ServiceType.CHILD_CARE)
                .buildRegisterCWCForm(registerCWCForm);

    }
}
