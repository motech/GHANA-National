package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.CareHistoryVO;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.fail;
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
    public void shouldRethrowException() throws ObservationNotFoundException {
        doThrow(new RuntimeException()).when(mockCareService).addCareHistory(Matchers.<CareHistoryVO>any());
        try {
            careHistoryFormHandler.handleFormEvent(new CareHistoryForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered error while processing care history form"));
        }
    }

    @Test
    public void shouldCreateEncounterAndObservationsForCareHistory() throws ObservationNotFoundException {
        String facilityMotechId = "facility motech Id";
        String facilityId = "facility id";
        final CareHistoryForm careHistoryForm = careHistoryFormWithCwcDetails(facilityMotechId);
        Facility facility = new Facility().mrsFacilityId(facilityId);
        when(mockFacilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(facility);

        careHistoryFormHandler.handleFormEvent(careHistoryForm);

        ArgumentCaptor<CareHistoryVO> careHistoryVOArgumentCaptor = ArgumentCaptor.forClass(CareHistoryVO.class);
        verify(mockCareService).addCareHistory(careHistoryVOArgumentCaptor.capture());

        assertCareHistoryDetails(careHistoryVOArgumentCaptor.getValue(), careHistoryForm, facilityId);
    }

    @Test
    public void shouldLogErrorIncaseOfErrorWhileProcessing() {
        careHistoryFormHandler.log = mock(Logger.class);
        MotechException mockError = mock(MotechException.class);
        String motechId = "motechId";
        when(mockFacilityService.getFacilityByMotechId(anyString())).thenThrow(mockError);
        try {
            CareHistoryForm careHistoryForm=new CareHistoryForm();
            careHistoryForm.setMotechId(motechId);
            careHistoryFormHandler.handleFormEvent(careHistoryForm);
        } catch (XFormHandlerException e) {
            //expected
        }
        verify(careHistoryFormHandler.log).error("Encountered error while processing care history form for patientId:"+motechId, mockError);
    }

    public static void assertCareHistoryDetails(CareHistoryVO careHistoryVO, CareHistoryForm careHistoryForm, String facilityId) {
        assertThat(careHistoryVO.getStaffId(), is(equalTo(careHistoryForm.getStaffId())));
        assertThat(careHistoryVO.getFacilityId(), is(equalTo(facilityId)));
        assertThat(careHistoryVO.getPatientMotechId(), is(equalTo(careHistoryForm.getMotechId())));
        assertThat(careHistoryVO.getDate(), is(equalTo(careHistoryForm.getDate())));
        assertANCCareHistoryDetails(careHistoryForm.getANCCareHistories(),
                careHistoryForm.getLastIPT(), careHistoryForm.getLastIPTDate(), careHistoryForm.getLastTT(),
                careHistoryForm.getLastTTDate(), careHistoryVO.getAncCareHistoryVO());

        assertCwcCareHistoryDetails(careHistoryForm.getCWCCareHistories(), careHistoryForm.getBcgDate(),
                careHistoryForm.getLastVitaminADate(), careHistoryForm.getMeaslesDate(), careHistoryForm.getLastMeasles(), careHistoryForm.getYellowFeverDate(),
                careHistoryForm.getLastPentaDate(), careHistoryForm.getLastOPVDate(), careHistoryForm.getLastIPTIDate(),
                careHistoryForm.getLastRotavirusDate(), careHistoryForm.getLastPneumococcalDate(), careHistoryForm.getLastVitaminA(), careHistoryForm.getLastPenta(), careHistoryForm.getLastOPV(),
                careHistoryForm.getLastRotavirus(),careHistoryForm.getLastPneumococcal(), careHistoryVO.getCwcCareHistoryVO());
    }

    public static CareHistoryForm careHistoryFormWithCwcDetails(String facilityMotechId) {
        CareHistoryForm careHistoryForm = new CareHistoryForm();
        careHistoryForm.setAddHistory("VITA_A IPTI BCG OPV PENTA MEASLES YF IPT_SP TT");
        careHistoryForm.setFacilityId(facilityMotechId);
        careHistoryForm.setStaffId("staffId");
        careHistoryForm.setMotechId("motechId");
        careHistoryForm.setLastOPV(0);
        careHistoryForm.setLastOPVDate(DateUtil.newDate(2001, 12, 1).toDate());
        careHistoryForm.setLastPenta(1);
        careHistoryForm.setLastPentaDate(DateUtil.newDate(2001, 12, 2).toDate());
        careHistoryForm.setLastIPTI(1);
        careHistoryForm.setLastIPTIDate(DateUtil.newDate(2001, 12, 3).toDate());
        careHistoryForm.setLastVitaminA("blue");
        careHistoryForm.setLastVitaminADate(DateUtil.newDate(2001, 12, 4).toDate());
        careHistoryForm.setMeaslesDate(DateUtil.newDate(2001, 12, 5).toDate());
        careHistoryForm.setYellowFeverDate(DateUtil.newDate(2001, 12, 6).toDate());
        careHistoryForm.setBcgDate(DateUtil.newDate(2001, 12, 7).toDate());
        careHistoryForm.setLastPneumococcal(1);
        careHistoryForm.setLastPneumococcalDate(DateUtil.newDate(2001, 12, 7).toDate());
        careHistoryForm.setLastRotavirus(1);
        careHistoryForm.setLastRotavirusDate(DateUtil.newDate(2001, 12, 7).toDate());
        careHistoryForm.setDate(DateUtil.newDate(2001, 12, 8).toDate());
        return careHistoryForm;
    }
}
