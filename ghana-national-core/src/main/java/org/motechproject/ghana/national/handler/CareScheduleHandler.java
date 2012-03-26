package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
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
    public CareScheduleHandler(PatientService patientService, FacilityService facilityService, SMSGateway smsGateway) {
        super(patientService, facilityService, smsGateway);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePregnancyAlert(final MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleTTVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleBCGAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(BCG_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleAncVisitAlert(MotechEvent motechEvent) {
        sendAggregativeSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleIPTpVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleIPTiVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleMeaslesVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePentaVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleYellowFeverVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregativeSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handleOpvVaccinationAlert(final MilestoneEvent milestoneEvent) {
       sendAggregativeSMSToFacility(CWC_OPV_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePNCMotherAlert(final MilestoneEvent milestoneEvent) {
        sendInstantSMSToFacility(PNC_MOTHER_SMS_KEY, milestoneEvent);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePNCChildAlert(final MilestoneEvent milestoneEvent) {
        sendInstantSMSToFacility(PNC_CHILD_SMS_KEY, milestoneEvent);
    }
}
