package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.model.DayOfWeek;
import org.motechproject.retry.service.RetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MobileMidwifeService {

    private AllMobileMidwifeEnrollments allEnrollments;
    private AllCampaigns allCampaigns;
    private AllPatientsOutbox allPatientsOutbox;
    private RetryService retryService;
    private AllPatients allPatients;

    @Autowired
    public MobileMidwifeService(AllMobileMidwifeEnrollments allEnrollments, AllCampaigns allCampaigns, AllPatientsOutbox allPatientsOutbox, RetryService retryService, AllPatients allPatients) {
        this.allEnrollments = allEnrollments;
        this.allCampaigns = allCampaigns;
        this.allPatientsOutbox = allPatientsOutbox;
        this.retryService = retryService;
        this.allPatients = allPatients;
    }

    public void register(MobileMidwifeEnrollment enrollment) {
        unRegister(enrollment.getPatientId());
        enrollment.setActive(true);
        allEnrollments.add(enrollment);
        startMobileMidwifeCampaign(enrollment);
    }

    public void startMobileMidwifeCampaign(MobileMidwifeEnrollment enrollment) {
        if (enrollment.campaignApplicable()) {
            LocalDate referenceDate;
            if (enrollment.getMedium().equals(Medium.SMS)) {
//                referenceDate = allCampaigns.nextCycleDateFromToday(enrollment.getServiceType(), Medium.SMS);
                allCampaigns.start(enrollment.createCampaignRequestForTextMessage(enrollment.getEnrollmentDateTime().toLocalDate()));
            } else if (enrollment.getMedium().equals(Medium.VOICE)) {
                DayOfWeek applicableDays = enrollment.getDayOfWeek() != null ? enrollment.getDayOfWeek() : DayOfWeek.getDayOfWeek(enrollment.getEnrollmentDateTime().dayOfWeek());
                referenceDate = getReferenceDate(enrollment.getEnrollmentDateTime(), applicableDays);
                allCampaigns.start(enrollment.createCampaignRequestForVoiceMessage(referenceDate, enrollment.getTimeOfDay()));
            }
        }
    }

    //moving reference date according to startOffset such that schedule start date lies on next applicable day
    private LocalDate getReferenceDate(DateTime enrollmentDateTime, DayOfWeek applicableDays) {
        return Utility.nextApplicableWeekDay(enrollmentDateTime, Arrays.asList(applicableDays)).toLocalDate();
    }

    public void unRegister(String patientId) {
        MobileMidwifeEnrollment enrollment = findActiveBy(patientId);
        if (enrollment != null) {
            enrollment.setActive(false);
            allEnrollments.update(enrollment);
            if (enrollment.campaignApplicable()) {
                allCampaigns.stop(enrollment.campaignRequest());
                allPatientsOutbox.removeMobileMidwifeMessages(patientId);
                retryService.fulfill(patientId, Constants.RETRY_GROUP);
            }
        }
    }

    public MobileMidwifeEnrollment findActiveBy(String patientId) {
        return allEnrollments.findActiveBy(patientId);
    }

    public MobileMidwifeEnrollment findLatestEnrollment(String patientId) {
        return allEnrollments.findLatestEnrollment(patientId);
    }

    public MobileMidwifeEnrollment findMotherMobileMidwifeEnrollment(String motechId){
        Patient mother = allPatients.getMother(motechId);
        MobileMidwifeEnrollment motherMobileMidwifeEnrollment = (mother != null) ? allEnrollments.findActiveBy(mother.getMotechId()) : null;
        return motherMobileMidwifeEnrollment;
    }

    public void rollover(String motechId, DateTime enrollmentDate) {
        MobileMidwifeEnrollment activeMobileMidwifeEnrollment = findActiveBy(motechId);
        if (activeMobileMidwifeEnrollment == null || activeMobileMidwifeEnrollment.getServiceType().equals(ServiceType.CHILD_CARE))
            return;
        unRegister(motechId);
        MobileMidwifeEnrollment newMobileMidwifeEnrollment = MobileMidwifeEnrollment.cloneNew(activeMobileMidwifeEnrollment);
        newMobileMidwifeEnrollment.setEnrollmentDateTime(enrollmentDate);
        newMobileMidwifeEnrollment.setServiceType(ServiceType.CHILD_CARE);
        newMobileMidwifeEnrollment.setMessageStartWeek("41");
        register(newMobileMidwifeEnrollment);
    }
}
