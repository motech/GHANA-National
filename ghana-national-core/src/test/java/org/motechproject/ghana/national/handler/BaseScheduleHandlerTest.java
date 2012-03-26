package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.SMSTemplateTest.assertContainsTemplateValues;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.PNC_CHILD_SMS_KEY;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.PREGNANCY_ALERT_SMS_KEY;

public class BaseScheduleHandlerTest {

    @Mock
    private PatientService mockPatientService;
    @Mock
    private FacilityService mockFacilityService;
    @Mock
    private SMSGateway SMSGateway;

    private CareScheduleHandler careScheduleHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandler = new CareScheduleHandler(mockPatientService, mockFacilityService, SMSGateway);
    }

    @Test
    public void shouldSendAggregativeSMSToFacilityForAnAppointment() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String ancVisitKey = "ancVisitKey";
        final String facilityId = "facilityid";
        final String patientMotechId = "patientmotechid";
        final String firstName = "firstName";
        final String lastname = "lastname";
        final String visitName = "ancVisit";

        parameters.put(EventKeys.EXTERNAL_ID_KEY, patientMotechId);
        parameters.put(EventKeys.VISIT_NAME, visitName);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, visitName + "3");

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastname).dateOfBirth(DateUtil.newDate(1999, 3, 3).toDate());
        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(new Patient(new MRSPatient(patientMotechId, person, new MRSFacility(facilityId))));

        final String phoneNumber = "phoneNumber";
        when(mockFacilityService.getFacility(facilityId)).thenReturn(new Facility().phoneNumber(phoneNumber));

        careScheduleHandler.sendAggregativeSMSToFacilityForAnAppointment(ancVisitKey, new MotechEvent("subject", parameters));

        ArgumentCaptor<Map> templateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        verify(SMSGateway).dispatchSMSToAggregator(eq(ancVisitKey), templateValuesArgCaptor.capture(), eq(phoneNumber));

        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, patientMotechId);
            put(WINDOW, "Overdue");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(SCHEDULE_NAME, visitName);
        }}, templateValuesArgCaptor.getValue());
    }

    @Test
    public void shouldSendAggregativeSMSToFacility() {

        final String patientId = "patientid";
        final String facilityId = "facilityid";
        final String patientMotechId = "patientmotechid";
        final String firstName = "firstName";
        final String lastname = "lastname";
        final String windowName = WindowName.due.name();
        final String scheduleName = "edd";

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastname).dateOfBirth(DateUtil.newDate(2000, 1, 1).toDate());
        when(mockPatientService.patientByOpenmrsId(patientId)).thenReturn(new Patient(new MRSPatient(patientMotechId, person, new MRSFacility(facilityId))));

        final String phoneNumber = "phoneNumber";
        when(mockFacilityService.getFacility(facilityId)).thenReturn(new Facility().phoneNumber(phoneNumber));

        MilestoneEvent milestoneEvent = new MilestoneEvent(patientId, scheduleName, null, windowName, null);
        careScheduleHandler.sendAggregativeSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);

        ArgumentCaptor<Map> templateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        verify(SMSGateway).dispatchSMSToAggregator(eq(PREGNANCY_ALERT_SMS_KEY), templateValuesArgCaptor.capture(), eq(phoneNumber));
        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, patientMotechId);
            put(WINDOW, "Due");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(SCHEDULE_NAME, scheduleName);
        }}, templateValuesArgCaptor.getValue());
    }

    @Test
    public void shouldSendInstantSMSToFacility() {

        final String patientId = "patientid11";
        final String facilityId = "facilityid22";
        final String patientMotechId = "patientmotechid33";
        final String firstName = "firstName44";
        final String lastname = "lastname55";
        final String windowName = WindowName.late.name();
        final String scheduleName = "some schedule";

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastname).dateOfBirth(DateUtil.newDate(2000, 1, 1).toDate());
        when(mockPatientService.patientByOpenmrsId(patientId)).thenReturn(new Patient(new MRSPatient(patientMotechId, person, new MRSFacility(facilityId))));

        final String phoneNumber = "phoneNumber";
        when(mockFacilityService.getFacility(facilityId)).thenReturn(new Facility().phoneNumber(phoneNumber));

        MilestoneEvent milestoneEvent = new MilestoneEvent(patientId, scheduleName, null, windowName, null);

        careScheduleHandler.sendInstantSMSToFacility(PNC_CHILD_SMS_KEY, milestoneEvent);

        ArgumentCaptor<Map> templateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        verify(SMSGateway).dispatchSMS(eq(PNC_CHILD_SMS_KEY), templateValuesArgCaptor.capture(), eq(phoneNumber));
        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, patientMotechId);
            put(WINDOW, "Overdue");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(SCHEDULE_NAME, scheduleName);
        }}, templateValuesArgCaptor.getValue());
    }
}
