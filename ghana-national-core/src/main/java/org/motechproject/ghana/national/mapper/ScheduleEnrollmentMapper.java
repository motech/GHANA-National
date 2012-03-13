package org.motechproject.ghana.national.mapper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;

import static org.motechproject.util.DateUtil.time;

public class ScheduleEnrollmentMapper {
    public EnrollmentRequest map(Patient patient, PatientCare patientCare, LocalDate enrollmentDate) {
        return new EnrollmentRequest(patient.getMRSPatientId(), patientCare.name(),
                patientCare.preferredTime(), patientCare.startingOn(), patientCare.referenceTime(), enrollmentDate, null, null);
    }

    public EnrollmentRequest map(Patient patient, PatientCare patientCare, DateTime enrollmentDateTime) {
        return new EnrollmentRequest(patient.getMRSPatientId(), patientCare.name(),
                patientCare.preferredTime(), patientCare.startingOn(), patientCare.referenceTime(), enrollmentDateTime.toLocalDate(), time(enrollmentDateTime), null);
    }

    public EnrollmentRequest map(Patient patient, PatientCare patientCare, LocalDate enrollmentDate, String startingMilestone) {
        return new EnrollmentRequest(patient.getMRSPatientId(), patientCare.name(),
                patientCare.preferredTime(), patientCare.startingOn(), patientCare.referenceTime(), enrollmentDate, null, startingMilestone);
    }

    public EnrollmentRequest map(String mrsPatientId, String programName) {
        return new EnrollmentRequest(mrsPatientId, programName, null, null, null, null, null, null);
    }
}
