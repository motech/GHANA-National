package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMidwifeService {

    private AllMobileMidwifeEnrollments allEnrollments;
    private AllCampaigns allCampaigns;

    @Autowired
    public MobileMidwifeService(AllMobileMidwifeEnrollments allEnrollments, AllCampaigns allCampaigns) {
        this.allEnrollments = allEnrollments;
        this.allCampaigns = allCampaigns;
    }

    public void register(MobileMidwifeEnrollment enrollment) {
        unRegister(enrollment.getPatientId());
        enrollment.setActive(true);
        allEnrollments.add(enrollment);

        startMobileMidwifeCampaign(enrollment);
    }

    public void startMobileMidwifeCampaign(MobileMidwifeEnrollment enrollment) {
        if (enrollment.campaignApplicable()) {
            LocalDate nextApplicableDay = allCampaigns.nextCycleDateFromToday(enrollment.getServiceType());
            allCampaigns.start(enrollment.createCampaignRequest(nextApplicableDay));
        }
    }

    public void unRegister(String patientId) {
        MobileMidwifeEnrollment enrollment = findActiveBy(patientId);
        if (enrollment != null) {
            enrollment.setActive(false);
            allEnrollments.update(enrollment);
            if (enrollment.campaignApplicable()) allCampaigns.stop(enrollment.stopCampaignRequest());
        }
    }

    public MobileMidwifeEnrollment findActiveBy(String patientId) {
        return allEnrollments.findActiveBy(patientId);
    }

    public MobileMidwifeEnrollment findLatestEnrollment(String patientId) {
        return allEnrollments.findLatestEnrollment(patientId);
    }

    public void rollover(String motechId,DateTime enrollmentDate) {
        MobileMidwifeEnrollment activeMobileMidwifeEnrollment = findActiveBy(motechId);
        if(activeMobileMidwifeEnrollment==null || activeMobileMidwifeEnrollment.getServiceType().equals(ServiceType.CHILD_CARE)) return;
        unRegister(motechId);
        MobileMidwifeEnrollment newMobileMidwifeEnrollment = MobileMidwifeEnrollment.cloneNew(activeMobileMidwifeEnrollment);
        newMobileMidwifeEnrollment.setEnrollmentDateTime(enrollmentDate);
        newMobileMidwifeEnrollment.setServiceType(ServiceType.CHILD_CARE);
        newMobileMidwifeEnrollment.setMessageStartWeek("41");
        register(newMobileMidwifeEnrollment);
    }
}
