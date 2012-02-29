package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

public abstract class BaseScheduleHandler {

    protected AllPatients allPatients;
    protected SMSGateway smsGateway;
    protected AllFacilities allFacilities;

    protected BaseScheduleHandler() {
    }

    protected BaseScheduleHandler(AllPatients allPatients, AllFacilities allFacilities, SMSGateway smsGateway) {
        this.allPatients = allPatients;
        this.smsGateway = smsGateway;
        this.allFacilities = allFacilities;
    }

    protected void sendSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        String externalId = milestoneEvent.getExternalId();
        final Patient patient = allPatients.patientByOpenmrsId(externalId);
        final String motechId = patient.getMrsPatient().getMotechId();

        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        smsGateway.dispatchSMSToAggregator(smsTemplateKey, new HashMap<String, String>() {{
            put(MOTECH_ID, motechId);
            put(WINDOW, AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName());
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(SCHEDULE_NAME, milestoneEvent.getScheduleName());
        }}, facility.getPhoneNumber());
    }

}
