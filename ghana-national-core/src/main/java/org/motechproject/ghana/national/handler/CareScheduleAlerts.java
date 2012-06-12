package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.repository.*;
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
public class CareScheduleAlerts extends BaseScheduleHandler implements CareScheduleHandler {

    public CareScheduleAlerts() {
    }

    @Autowired
    public CareScheduleAlerts(PatientService patientService, FacilityService facilityService, SMSGateway smsGateway,
                              VoiceGateway voiceGateway, AllObservations allObservations,
                              AllMobileMidwifeEnrollments allMobileMidwifeEnrollments,AllPatientsOutbox allPatientsOutbox,
                              ScheduleJsonReader scheduleJsonReader) {
        super(patientService, facilityService, smsGateway, voiceGateway, allObservations, allMobileMidwifeEnrollments, allPatientsOutbox, scheduleJsonReader);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handlePregnancyAlert(final MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleTTVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_TT, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleBCGAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(BCG_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_BCG, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleAncVisitAlert(MotechEvent motechEvent) {
        sendAggregatedSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
        sendAggregatedSMSToPatientForAppointment(PATIENT_ANC_VISIT, motechEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleIPTpVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_IPT, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleIPTiVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_IPTI, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleMeaslesVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_MEASLES, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handlePentaVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_PENTA, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleYellowFeverVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_YELLOW_FEVER, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleOpvVaccinationAlert(final MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(CWC_OPV_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_OPV, milestoneEvent);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handlePNCMotherAlert(final MilestoneEvent milestoneEvent) {
        sendInstantSMSToFacility(PNC_MOTHER_SMS_KEY, milestoneEvent);
        sendInstantMessageToPatient(PATIENT_PNC_MOTHER, milestoneEvent, AlertType.CARE);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handlePNCChildAlert(final MilestoneEvent milestoneEvent) {
        sendInstantSMSToFacility(PNC_CHILD_SMS_KEY, milestoneEvent);
        sendInstantMessageToPatient(PATIENT_PNC_BABY, milestoneEvent, AlertType.CARE);
    }

    @Override
    @LoginAsAdmin
    @ApiSession
    public void handleRotavirusVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendAggregatedSMSToFacility(CWC_ROTAVIRUS_SMS_KEY, milestoneEvent);
        sendAggregatedMessageToPatient(PATIENT_ROTAVIRUS, milestoneEvent);
    }


}
