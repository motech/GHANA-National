package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMidwifeService {

    @Autowired
    private AllMobileMidwifeEnrollments allEnrollments;
    @Autowired
    private MobileMidwifeCampaign mobileMidwifeCampaign;

    public void register(MobileMidwifeEnrollment enrollment) {
        unregister(enrollment.getPatientId());
        enrollment.setActive(true);
        allEnrollments.add(enrollment);
        if (enrollment.getConsent()) {
            mobileMidwifeCampaign.start(enrollment);
        }
    }

    public void unregister(String patientId) {
        MobileMidwifeEnrollment midwifeEnrollment = findActiveBy(patientId);
        if (midwifeEnrollment != null) {
            midwifeEnrollment.setActive(false);
            allEnrollments.update(midwifeEnrollment);
            if (midwifeEnrollment.getConsent()) mobileMidwifeCampaign.stop(midwifeEnrollment);
        }
    }

    public MobileMidwifeEnrollment findActiveBy(String patientId) {
        return allEnrollments.findActiveBy(patientId);
    }

    public MobileMidwifeEnrollment findLatestEnrollment(String patientId) {
        return allEnrollments.findLatestEnrollment(patientId);
    }
}
