package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Enrollment;
import org.motechproject.ghana.national.repository.AllEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

    private AllEnrollment allEnrollments;
    @Autowired
    public EnrollmentService(AllEnrollment allEnrollments) {
        this.allEnrollments = allEnrollments;
    }

    public Enrollment enrollmentFor(String patientId) {
        return allEnrollments.findBy(patientId);
    }
}
