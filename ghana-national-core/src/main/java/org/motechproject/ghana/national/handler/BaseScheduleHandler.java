package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

public abstract class BaseScheduleHandler {

    protected AllPatients allPatients;
    protected TextMessageService textMessageService;
    protected AllFacilities allFacilities;

    protected BaseScheduleHandler() {
    }

    protected BaseScheduleHandler(AllPatients allPatients, AllFacilities allFacilities, TextMessageService textMessageService) {
        this.allPatients = allPatients;
        this.textMessageService = textMessageService;
        this.allFacilities = allFacilities;
    }

    protected void sendSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent, final LocalDate dueDate) {
        String externalId = milestoneEvent.getExternalId();
        final Patient patient = allPatients.patientByOpenmrsId(externalId);
        final String motechId = patient.getMrsPatient().getMotechId();

        SMS sms = textMessageService.getSMS(smsTemplateKey, new HashMap<String, String>() {{
            put(MOTECH_ID, motechId);
            put(DUE_DATE, dueDate.toString());
            put(WINDOW, getWindowName(milestoneEvent.getWindowName()));
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(SCHEDULE_NAME, milestoneEvent.getScheduleName());
        }});

        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());
        textMessageService.sendSMS(facility, sms);
    }

    private String getWindowName(String windowName) {
        return windowName.equals(WindowName.earliest.name()) ? "Upcoming" : (windowName.equals(WindowName.due.name()) ? "Due" : "Overdue");
    }

    protected LocalDate addAggregationPeriodTo(LocalDate dueDate) {
        return dueDate.plusWeeks(1);
    }
}
