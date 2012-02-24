package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.CareHistoryVO;
import org.motechproject.model.MotechEvent;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.handlers.RegisterANCFormHandlerTest.assertANCCareHistoryDetails;
import static org.motechproject.ghana.national.handlers.RegisterCWCFormHandlerTest.assertCwcCareHistoryDetails;

public class CareHistoryFormHandlerTest {
    private CareHistoryFormHandler careHistoryFormHandler;
    @Mock
    private CareService mockCareService;
    @Mock
    private FacilityService mockFacilityService;

    @Before
    public void setUp() {
        initMocks(this);
        careHistoryFormHandler = new CareHistoryFormHandler();
        ReflectionTestUtils.setField(careHistoryFormHandler, "careService", mockCareService);
        ReflectionTestUtils.setField(careHistoryFormHandler, "facilityService", mockFacilityService);
    }

    @Test
    public void shouldCreateEncounterAndObservationsForCareHistory() {
        String facilityMotechId = "facility motech Id";
        String facilityId = "facility id";
        final CareHistoryForm careHistoryForm = careHistoryFormWithCwcDetails(facilityMotechId);
        Facility facility = new Facility().mrsFacilityId(facilityId);
        when(mockFacilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(facility);

        Map<String, Object> parameter = new HashMap<String, Object>(){{
            put("formBean", careHistoryForm);}};
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.careHistory", parameter);
        careHistoryFormHandler.handleFormEvent(event);

        ArgumentCaptor<CareHistoryVO> careHistoryVOArgumentCaptor = ArgumentCaptor.forClass(CareHistoryVO.class);
        verify(mockCareService).addCareHistory(careHistoryVOArgumentCaptor.capture());

        assertCareHistoryDeatils(careHistoryVOArgumentCaptor.getValue(), careHistoryForm, facilityId);
    }

    @Test
    public void shouldLogErrorIncaseOfErrorWhileProcessing(){
        careHistoryFormHandler.log = mock(Logger.class);
        MotechException mockError = mock(MotechException.class);
        when(mockFacilityService.getFacilityByMotechId(anyString())).thenThrow(mockError);
        careHistoryFormHandler.handleFormEvent(new MotechEvent("form.validation.successful.NurseDataEntry.careHistory", new HashMap<String, Object>(){{put("formBean", new CareHistoryForm());}}));
        verify(careHistoryFormHandler.log).error("Encountered error while saving care history details", mockError);
    }

    public static void assertCareHistoryDeatils(CareHistoryVO careHistoryVO, CareHistoryForm careHistoryForm, String facilityId) {
        assertThat(careHistoryVO.getStaffId(), is(equalTo(careHistoryForm.getStaffId())));
        assertThat(careHistoryVO.getFacilityId(), is(equalTo(facilityId)));
        assertThat(careHistoryVO.getPatientMotechId(), is(equalTo(careHistoryForm.getMotechId())));
        assertThat(careHistoryVO.getDate(), is(equalTo(careHistoryForm.getDate())));
        assertANCCareHistoryDetails(careHistoryForm.getANCCareHistories(),
                careHistoryForm.getLastIPT(), careHistoryForm.getLastIPTDate(), careHistoryForm.getLastTT(),
                careHistoryForm.getLastTTDate(), careHistoryVO.getAncCareHistoryVO());

        assertCwcCareHistoryDetails(careHistoryForm.getCWCCareHistories(), careHistoryForm.getBcgDate(),
                careHistoryForm.getLastVitaminADate(), careHistoryForm.getMeaslesDate(), careHistoryForm.getYellowFeverDate(),
                careHistoryForm.getLastPentaDate(), careHistoryForm.getLastOPVDate(), careHistoryForm.getLastIPTDate(),
                careHistoryForm.getLastPenta(), careHistoryForm.getLastOPV(), careHistoryVO.getCwcCareHistoryVO());
    }

    public static CareHistoryForm careHistoryFormWithCwcDetails(String facilityMotechId) {
        CareHistoryForm careHistoryForm = new CareHistoryForm();
        careHistoryForm.setAddHistory("VITA_A IPTI BCG OPV PENTA MEASLES YF IPT TT");
        careHistoryForm.setFacilityId(facilityMotechId);
        careHistoryForm.setStaffId("staffId");
        careHistoryForm.setMotechId("motechId");
        careHistoryForm.setLastOPV(0);
        careHistoryForm.setLastOPVDate(DateUtil.newDate(2001, 12, 1).toDate());
        careHistoryForm.setLastPenta(1);
        careHistoryForm.setLastPentaDate(DateUtil.newDate(2001, 12, 2).toDate());
        careHistoryForm.setLastIPTI(1);
        careHistoryForm.setLastIPTIDate(DateUtil.newDate(2001, 12, 3).toDate());
        careHistoryForm.setLastVitaminADate(DateUtil.newDate(2001, 12, 4).toDate());
        careHistoryForm.setMeaslesDate(DateUtil.newDate(2001, 12, 5).toDate());
        careHistoryForm.setYellowFeverDate(DateUtil.newDate(2001, 12, 6).toDate());
        careHistoryForm.setBcgDate(DateUtil.newDate(2001, 12, 7).toDate());
        careHistoryForm.setDate(DateUtil.newDate(2001, 12, 8).toDate());
        return careHistoryForm;
    }
}
