package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.EDD;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.MOTECH_ID;
import static org.motechproject.ghana.national.handler.CareScheduleHandler.*;

public class CareScheduleHandlerTest {
    AllPatients allPatients = mock(AllPatients.class);
    TextMessageService textMessageService;
    AllFacilities allFacilities;

    @Test
    public void handlePregnancyAlert() {
        textMessageService = mock(TextMessageService.class);
        allFacilities = mock(AllFacilities.class);
        CareScheduleHandler careScheduleHandler = new CareScheduleHandler(allPatients, textMessageService, allFacilities);
        String patientId = "123";
        String facilityId = "234";

        when(allPatients.patientByOpenmrsId(patientId)).thenReturn(new Patient(new MRSPatient("motechid", null, new MRSFacility(facilityId))));
        Facility facility = new Facility().phoneNumber("phonenumber");
        when(allFacilities.getFacility(facilityId)).thenReturn(facility);

        final LocalDate edd = DateUtil.today();
        LocalDate concievedDate = edd.minusWeeks(40);

        when(textMessageService.getSMS(PREGNANCY_ALERT_SMS_KEY, new HashMap<String, String>() {{
            put(MOTECH_ID, "motechid");
            put(EDD, edd.toString());
        }})).thenReturn(SMS.fromSMSText("motechid, " + edd.toString()));

        careScheduleHandler.handlePregnancyAlert(new MilestoneEvent(patientId, "Pregnancy", "Default", "Upcoming", concievedDate));

        verify(textMessageService).sendSMS(facility, SMS.fromSMSText("motechid, " + edd.toString()));
    }
}
