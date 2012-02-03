package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.CareScheduleNames;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.EDD;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.MOTECH_ID;

@Component
public class CareScheduleHandler {
    private AllPatients allPatients;
    private TextMessageService textMessageService;
    private AllFacilities allFacilities;

    @Autowired
    public CareScheduleHandler(AllPatients allPatients, TextMessageService textMessageService, AllFacilities allFacilities) {
        this.allPatients = allPatients;
        this.textMessageService = textMessageService;
        this.allFacilities = allFacilities;
    }

    @MotechListener(subjects = {EventSubject.MILESTONE_ALERT})
    public void handleAlert(MotechEvent motechEvent) {
        MilestoneEvent milestoneEvent = new MilestoneEvent(motechEvent);
        if (milestoneEvent.getScheduleName().equals(CareScheduleNames.DELIVERY))
            handlePregnancyAlert(milestoneEvent);
    }

    public void handlePregnancyAlert(MilestoneEvent milestoneEvent) {
        String externalId = milestoneEvent.getExternalId();
        LocalDate conceptionDate = milestoneEvent.getReferenceDate();

        Patient patient = allPatients.patientByOpenmrsId(externalId);
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate);
        LocalDate dateOfDelivery = pregnancy.dateOfDelivery();
        String motechId = patient.getMrsPatient().getMotechId();
        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        String messageTemplate = String.format("Pregancy Due: %s, %s", MOTECH_ID, EDD);
        textMessageService.sendSMS(facility, messageTemplate.replace(MOTECH_ID, motechId).replace(EDD, dateOfDelivery.toString()));
    }
}
