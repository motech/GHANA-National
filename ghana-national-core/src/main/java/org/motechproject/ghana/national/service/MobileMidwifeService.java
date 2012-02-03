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
        deactivateExistingEnrollmentAndCampaign(enrollment.getPatientId());
        enrollment.setActive(true);
        allEnrollments.add(enrollment);
        if(enrollment.getConsent()) {
            mobileMidwifeCampaign.start(enrollment);
        }
    }

    private void deactivateExistingEnrollmentAndCampaign(String patientId) {
        MobileMidwifeEnrollment existingEnrollment = findBy(patientId);
        if(existingEnrollment != null) {
            existingEnrollment.setActive(false);
            allEnrollments.update(existingEnrollment);
            if (existingEnrollment.getConsent()) mobileMidwifeCampaign.stop(existingEnrollment);
        }
    }

    public MobileMidwifeEnrollment findBy(String patientId) {
        return allEnrollments.findByPatientId(patientId);
    }
}
