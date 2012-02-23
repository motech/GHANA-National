package org.motechproject.ghana.national.mapper;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

public class ScheduleEnrollmentMapper {
    public EnrollmentRequest map(Patient patient, PatientCare patientCare) {
        return new EnrollmentRequest(patient.getMRSPatientId(), patientCare.name(),
                new Time(DateUtil.now().toLocalTime()), patientCare.startingOn());
    }
}
