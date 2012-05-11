package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
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
            DateTime nextApplicableDay = allCampaigns.nearestCycleDate(enrollment);
            allCampaigns.start(enrollment.createCampaignRequest(nextApplicableDay.toLocalDate()));
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
}
