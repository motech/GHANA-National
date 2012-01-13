package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMidwifeService {

    @Autowired
    private AllMobileMidwifeEnrollments allEnrollments;

    public void saveOrUpdate(MobileMidwifeEnrollment enrollment) {
        allEnrollments.createOrUpdate(enrollment);
    }

    public MobileMidwifeEnrollment findBy(String patientId) {
        return allEnrollments.findByPatientId(patientId);
    }
}
