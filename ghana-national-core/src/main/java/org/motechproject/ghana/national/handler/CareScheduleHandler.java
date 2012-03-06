package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.*;

@Component
public class CareScheduleHandler extends BaseScheduleHandler {

    public CareScheduleHandler() {
    }

    @Autowired
    public CareScheduleHandler(AllPatients allPatients, AllFacilities allFacilities, SMSGateway smsGateway) {
        super(allPatients, allFacilities, smsGateway);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePregnancyAlert(final MilestoneEvent milestoneEvent) {
        sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleTTVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleBCGAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(BCG_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleAncVisitAlert(MotechEvent motechEvent) {
        sendSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleIPTpVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleIPTiVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleMeaslesVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePentaVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleYellowFeverVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePNCMotherAlert(final MilestoneEvent milestoneEvent) {
        final Patient patient = allPatients.patientByOpenmrsId(milestoneEvent.getExternalId());
        final MRSPatient mrsPatient = patient.getMrsPatient();
        final Facility facility = allFacilities.getFacility(mrsPatient.getFacility().getId());

        smsGateway.dispatchSMS(PNC_MOTHER_SMS_KEY, new HashMap<String, String>() {{
            put(MOTECH_ID, mrsPatient.getMotechId());
            put(WINDOW, AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName());
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(SCHEDULE_NAME, milestoneEvent.getScheduleName());
        }}, facility.phoneNumber());
    }
}
