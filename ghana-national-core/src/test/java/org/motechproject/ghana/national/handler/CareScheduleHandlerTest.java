package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.*;

public class CareScheduleHandlerTest {

    private CareScheduleHandler careScheduleHandlerSpy;
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private AllPatients mockAllPatients;
    @Mock
    private AllFacilities mockAllFacilities;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandlerSpy = spy(new CareScheduleHandler(mockAllPatients, mockAllFacilities, mockSmsGateway));
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandlePregnancyAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePregnancyAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleTTVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleTTVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleBCGAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleBCGAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(BCG_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleAncVisitAlert() {
        MotechEvent motechEvent = new MotechEvent("subject", new HashMap<String, Object>());
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);

        careScheduleHandlerSpy.handleAncVisitAlert(motechEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
    }

    @Test
    public void handleIPTpVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTpVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleIPTiVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTiVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleMeaslesVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleMeaslesVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleYellowFeverVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleYellowFeverVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handlePentaVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePentaVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handlePNCMotherAlert() {
        MilestoneAlert milestoneAlert = mock(MilestoneAlert.class);
        final MilestoneEvent milestoneEvent = new MilestoneEvent("1", "scheduleName", milestoneAlert, WindowName.due.name(), DateUtil.now());
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        Facility facility = mock(Facility.class);
        MRSFacility mrsFacility = mock(MRSFacility.class);
        String facilityId = "facilityId";

        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getFacility()).thenReturn(mrsFacility);
        when(mrsFacility.getId()).thenReturn(facilityId);
        when(mockAllPatients.patientByOpenmrsId(milestoneEvent.getExternalId())).thenReturn(mockPatient);
        when(mockAllFacilities.getFacility(facilityId)).thenReturn(facility);

        careScheduleHandlerSpy.handlePNCMotherAlert(milestoneEvent);
        verify(mockSmsGateway).dispatchSMS(eq(PNC_MOTHER_SMS_KEY), (Map<String, String>) any(), anyString());
    }
}
