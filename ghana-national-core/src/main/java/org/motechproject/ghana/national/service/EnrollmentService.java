package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.CWCEnrollment;
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

    public CWCEnrollment cwcEnrollmentFor(String patientId) {
        return allEnrollments.findBy(patientId);
    }

    public void saveOrUpdate(CWCEnrollment CWCEnrollment) {
        CWCEnrollment existingCWCEnrollment = allEnrollments.findBy(CWCEnrollment.getPatientId(), CWCEnrollment.getProgram().getName());
        if (existingCWCEnrollment != null) {
            existingCWCEnrollment.patienId(CWCEnrollment.getPatientId());
            existingCWCEnrollment.serialNumber(CWCEnrollment.getSerialNumber());
            existingCWCEnrollment.facilityId(CWCEnrollment.getFacilityId());
            existingCWCEnrollment.registrationDate(CWCEnrollment.getRegistrationDate());
            allEnrollments.update(existingCWCEnrollment);
        } else {
            allEnrollments.add(CWCEnrollment);
        }
    }
}
