package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMidwifeService {

    @Autowired
    private AllMobileMidwifeEnrollments allEnrollments;

    public void register(MobileMidwifeEnrollment enrollment) {
        MobileMidwifeEnrollment existingEnrollment = findBy(enrollment.getPatientId());
        if(existingEnrollment != null) {
            existingEnrollment.setActive(false);
            allEnrollments.update(existingEnrollment);
        }
        enrollment.setActive(true);
        allEnrollments.add(enrollment);
    }

    public MobileMidwifeEnrollment findBy(String patientId) {
        return allEnrollments.findByPatientId(patientId);
    }
}
