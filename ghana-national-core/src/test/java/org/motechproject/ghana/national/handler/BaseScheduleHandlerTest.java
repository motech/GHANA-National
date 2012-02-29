package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.PREGNANCY_ALERT_SMS_KEY;

public class BaseScheduleHandlerTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllFacilities allFacilities;
    @Mock
    private SMSGateway SMSGateway;

    private CareScheduleHandler careScheduleHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandler = new CareScheduleHandler(allPatients, allFacilities, SMSGateway);
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

        final String phoneNumber = "phoneNumber";
        when(allFacilities.getFacility(facilityId)).thenReturn(new Facility().phoneNumber(phoneNumber));


        MilestoneEvent milestoneEvent = new MilestoneEvent(patientId, scheduleName, null, windowName, null);

        careScheduleHandler.sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);

        verify(SMSGateway).dispatchSMSToAggregator(PREGNANCY_ALERT_SMS_KEY, new HashMap<String, String>() {{
            put(MOTECH_ID, patientMotechId);
            put(WINDOW, "Due");
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastname);
            put(SCHEDULE_NAME, scheduleName);
        }}, phoneNumber);
    }
}
