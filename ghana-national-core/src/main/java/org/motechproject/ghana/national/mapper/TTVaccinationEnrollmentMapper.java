package org.motechproject.ghana.national.mapper;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

public class TTVaccinationEnrollmentMapper {
    public EnrollmentRequest map(Patient patient, LocalDate vaccinationDate, String dosageMilestoneName) {
        return new EnrollmentRequest(patient.getMRSPatientId(), ScheduleNames.TT_VACCINATION_VISIT, new Time(DateUtil.now().toLocalTime()), vaccinationDate, dosageMilestoneName);
    }
}
