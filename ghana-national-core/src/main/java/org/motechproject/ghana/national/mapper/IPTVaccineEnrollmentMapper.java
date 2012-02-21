package org.motechproject.ghana.national.mapper;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;

public class IPTVaccineEnrollmentMapper {

    public EnrollmentRequest mapIPTp(Patient patient, LocalDate dateOfConception) {
        return new EnrollmentRequest(patient.getMRSPatientId(), ANC_IPT_VACCINE,
                new Time(DateUtil.now().toLocalTime()), dateOfConception);
    }
}
