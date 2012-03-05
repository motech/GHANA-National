package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void handleAncVisitAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(ANC_VISIT_SMS_KEY, milestoneEvent);
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
}
