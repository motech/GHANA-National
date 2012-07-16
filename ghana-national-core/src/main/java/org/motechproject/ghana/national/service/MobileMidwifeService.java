package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MobileMidwifeService {

    private AllMobileMidwifeEnrollments allEnrollments;
    private AllCampaigns allCampaigns;
    private AllPatientsOutbox allPatientsOutbox;

    @Autowired
    public MobileMidwifeService(AllMobileMidwifeEnrollments allEnrollments, AllCampaigns allCampaigns, AllPatientsOutbox allPatientsOutbox) {
        this.allEnrollments = allEnrollments;
        this.allCampaigns = allCampaigns;
        this.allPatientsOutbox= allPatientsOutbox;
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
                referenceDate = allCampaigns.nextCycleDateFromToday(enrollment.getServiceType(), Medium.SMS);
                allCampaigns.start(enrollment.createCampaignRequestForTextMessage(referenceDate));
            } else if (enrollment.getMedium().equals(Medium.VOICE)) {
                DayOfWeek applicableDays = enrollment.getDayOfWeek()!=null ? enrollment.getDayOfWeek() : DayOfWeek.getDayOfWeek(enrollment.getEnrollmentDateTime().dayOfWeek());
                referenceDate = getReferenceDate(enrollment.getEnrollmentDateTime(),applicableDays);
                allCampaigns.start(enrollment.createCampaignRequestForVoiceMessage(referenceDate, applicableDays, enrollment.getTimeOfDay()));
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
                CampaignRequest campaignRequest = enrollment.stopCampaignRequest();
                allCampaigns.stop(campaignRequest);
                allPatientsOutbox.removeMobileMidwifeMessages(patientId);
            }
        }
    }

    public MobileMidwifeEnrollment findActiveBy(String patientId) {
        return allEnrollments.findActiveBy(patientId);
    }

    public MobileMidwifeEnrollment findLatestEnrollment(String patientId) {
        return allEnrollments.findLatestEnrollment(patientId);
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
