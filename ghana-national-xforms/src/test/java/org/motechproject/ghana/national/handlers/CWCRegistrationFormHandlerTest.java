package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.service.CWCService;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.model.MotechEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCRegistrationFormHandlerTest {

    @Mock
    private CWCService mockCwcService;
    CWCRegistrationFormHandler formHandler;

    @Before
    public void setUp() {
         initMocks(this);
        formHandler = new CWCRegistrationFormHandler();
        ReflectionTestUtils.setField(formHandler, "cwcService", mockCwcService);
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
        final String facilityId = "3232";

        registerCWCForm.setStaffId(staffId);
        registerCWCForm.setFacilityId(facilityId);
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

        formHandler.handleFormEvent(new MotechEvent("", new HashMap<String, Object>(){{
            put("formBean", registerCWCForm);
        }}));

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
