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
        unregister(findBy(enrollment.getPatientId()));
        enrollment.setActive(true);
        allEnrollments.add(enrollment);
        if (enrollment.getConsent()) {
            mobileMidwifeCampaign.start(enrollment);
        }
    }

    public void unregister(MobileMidwifeEnrollment enrollment) {
        if (enrollment != null) {
            enrollment.setActive(false);
            allEnrollments.update(enrollment);
            if (enrollment.getConsent()) mobileMidwifeCampaign.stop(enrollment);
        }
    }

    public MobileMidwifeEnrollment findBy(String patientId) {
        return allEnrollments.findActiveByPatientId(patientId);
    }

    public MobileMidwifeEnrollment findLatestEnrollment(String patientId) {
        return allEnrollments.findByPatientId(patientId);
    }
}
