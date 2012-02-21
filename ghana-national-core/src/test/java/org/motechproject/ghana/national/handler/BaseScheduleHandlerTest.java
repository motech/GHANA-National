package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.handler.CareScheduleHandler.PREGNANCY_ALERT_SMS_KEY;

public class BaseScheduleHandlerTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllFacilities allFacilities;
    @Mock
    private TextMessageService textMessageService;

    private CareScheduleHandler careScheduleHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandler = new CareScheduleHandler(allPatients, allFacilities, textMessageService);
    }

    @Test
    public void shouldSendSMSToFacility(){

        final String patientId = "patientid";
        final String facilityId = "facilityid";
        final String patientMotechId = "patientmotechid";
        final String firstName = "firstName";
        final String lastname = "lastname";
        final String windowName = WindowName.due.name();
        final String scheduleName = "edd";

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastname);
        when(allPatients.patientByOpenmrsId(patientId)).thenReturn(new Patient(new MRSPatient(patientMotechId, person, new MRSFacility(facilityId))));

        Facility facilityMock = mock(Facility.class);
        when(allFacilities.getFacility(facilityId)).thenReturn(facilityMock);

        final LocalDate expectedDeliveryDate = DateUtil.newDate(2000, 1, 1);

        final SMS sms = SMS.fromSMSText("sms message");
        when(textMessageService.getSMS(PREGNANCY_ALERT_SMS_KEY, new HashMap<String, String>(){{
            put(MOTECH_ID, patientMotechId);
            put(DUE_DATE, expectedDeliveryDate.toString());
            put(WINDOW, "Due");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(SCHEDULE_NAME, scheduleName);
        }})).thenReturn(sms);

        MilestoneEvent milestoneEvent = new MilestoneEvent(patientId, scheduleName, null, windowName, null);

        careScheduleHandler.sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent, expectedDeliveryDate);

        verify(textMessageService).sendSMS(facilityMock, sms);

    }
}
