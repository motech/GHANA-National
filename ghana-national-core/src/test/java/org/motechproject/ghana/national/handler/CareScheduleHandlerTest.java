package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        LocalDate edd = DateUtil.today();
        LocalDate concievedDate = edd.minusWeeks(40);
        careScheduleHandler.handlePregnancyAlert(new MilestoneEvent(patientId, "Pregnancy", "Default", "Upcoming", concievedDate));

        verify(textMessageService).sendSMS(facility, "Pregancy Due: motechid, " + edd.toString());
    }
}
