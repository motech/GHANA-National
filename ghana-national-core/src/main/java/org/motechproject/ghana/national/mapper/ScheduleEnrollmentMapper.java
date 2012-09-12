package org.motechproject.ghana.national.mapper;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;

public class ScheduleEnrollmentMapper {
    public EnrollmentRequest map(Patient patient, PatientCare patientCare) {
        return new EnrollmentRequest().setExternalId(patient.getMRSPatientId())
                .setScheduleName(patientCare.name())
                .setPreferredAlertTime(patientCare.preferredTime())
                .setReferenceDate(patientCare.startingOn())
                .setReferenceTime(patientCare.referenceTime())
                .setEnrollmentDate(patientCare.enrollmentDate())
                .setEnrollmentTime(patientCare.enrollmentTime())
                .setStartingMilestoneName(patientCare.milestoneName())
                .setMetadata(patientCare.metaData());
    }

    public EnrollmentRequest map(String mrsPatientId, String programName) {
        return new EnrollmentRequest().setExternalId(mrsPatientId).setScheduleName(programName);
    }
}
