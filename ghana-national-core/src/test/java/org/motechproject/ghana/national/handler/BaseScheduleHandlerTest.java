package org.motechproject.ghana.national.handler;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
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
    private AllObservations mockAllObservations;
    @Mock
    private SMSGateway SMSGateway;

    private CareScheduleHandler careScheduleHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandler = new CareScheduleHandler(mockPatientService, mockFacilityService, SMSGateway, mockAllObservations);
    }

    @Test
    public void shouldSendAggregativeSMSToFacilityForAnAppointment() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String ancVisitKey = "ancVisitKey";
        final String facilityId = "facilityid";
        final String patientId = "patientmotechid";
        final String firstName = "firstName";
        final String lastname = "lastname";
        final String visitName = "ancVisit";

        parameters.put(EventKeys.EXTERNAL_ID_KEY, patientId);
        parameters.put(EventKeys.VISIT_NAME, visitName);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, visitName + "3");

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastname).dateOfBirth(DateUtil.newDate(1999, 3, 3).toDate());
        when(mockPatientService.getPatientByMotechId(patientId)).thenReturn(new Patient(new MRSPatient(patientId, person, new MRSFacility(facilityId))));

        final String phoneNumber = "phoneNumber";
        String additionalPhone = "addPhone";
        Facility facility = new Facility().phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhone);
        when(mockFacilityService.getFacility(facilityId)).thenReturn(facility);

        careScheduleHandler.sendAggregatedSMSToFacilityForAnAppointment(ancVisitKey, new MotechEvent("subject", parameters));

        ArgumentCaptor<Map> templateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(SMSGateway, times(2)).dispatchSMSToAggregator(eq(ancVisitKey), templateValuesArgCaptor.capture(), captor.capture(), eq(new AggregationMessageIdentifier(patientId, visitName).getIdentifier()));

        List<String> allPhoneNumbers = captor.getAllValues();
        assertEquals(2, allPhoneNumbers.size());
        assertEquals(facility.getPhoneNumbers(), allPhoneNumbers);

        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, patientId);
            put(WINDOW, "Overdue");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(MILESTONE_NAME, visitName);
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
        final String milestoneName="milestone1";
        final String serialNumber = "serialNumber";

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastname).dateOfBirth(DateUtil.newDate(2000, 1, 1).toDate());
        when(mockPatientService.patientByOpenmrsId(patientId)).thenReturn(new Patient(new MRSPatient(patientMotechId, person, new MRSFacility(facilityId))));

        final String phoneNumber = "phoneNumber";
        String additionalPhoneNumber = "addPhone";
        Facility facility = new Facility().phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhoneNumber);
        when(mockFacilityService.getFacility(facilityId)).thenReturn(facility);
        when(mockAllObservations.findObservation(patientMotechId, Concept.SERIAL_NUMBER.getName())).thenReturn(new MRSObservation<String>(new Date(), Concept.SERIAL_NUMBER.getName(), serialNumber));
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(new Milestone(milestoneName, Period.days(1), Period.days(1), Period.days(1), Period.days(1)), DateTime.now());
        MilestoneEvent milestoneEvent = new MilestoneEvent(patientId, scheduleName, milestoneAlert, windowName, null);
        careScheduleHandler.sendAggregatedSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> templateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        verify(SMSGateway, times(2)).dispatchSMSToAggregator(eq(PREGNANCY_ALERT_SMS_KEY), templateValuesArgCaptor.capture(), captor.capture(), eq(new AggregationMessageIdentifier(patientId, scheduleName).getIdentifier()));
        verify(mockAllObservations).findObservation(patientMotechId, Concept.SERIAL_NUMBER.getName());
        List<String> allPhoneNumbers = captor.getAllValues();
        assertEquals(2, allPhoneNumbers.size());
        assertEquals(facility.getPhoneNumbers(), allPhoneNumbers);
        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, patientMotechId);
            put(WINDOW, "Due");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(MILESTONE_NAME, milestoneName);
            put(SERIAL_NUMBER, serialNumber);
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
        String additionalPhoneNumber1 = "additionalPhone";
        Facility facility = new Facility().phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhoneNumber1);
        when(mockFacilityService.getFacility(facilityId)).thenReturn(facility);

        final String milestoneName="milestone1";
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(new Milestone(milestoneName, Period.days(1), Period.days(1), Period.days(1), Period.days(1)), DateTime.now());

        MilestoneEvent milestoneEvent = new MilestoneEvent(patientId, scheduleName, milestoneAlert, windowName, null);

        careScheduleHandler.sendInstantSMSToFacility(PNC_CHILD_SMS_KEY, milestoneEvent);

        ArgumentCaptor<Map> templateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(SMSGateway, times(2)).dispatchSMS(eq(PNC_CHILD_SMS_KEY), templateValuesArgCaptor.capture(), captor.capture());

        List<String> allPhoneNumbers = captor.getAllValues();
        assertEquals(2, allPhoneNumbers.size());
        assertEquals(facility.getPhoneNumbers(), allPhoneNumbers);
        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, patientMotechId);
            put(WINDOW, "Overdue");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(MILESTONE_NAME, milestoneName);
        }}, templateValuesArgCaptor.getValue());
    }
}
