package org.motechproject.ghana.national.mapper;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

public class TTVaccinationEnrollmentMapper {
    public EnrollmentRequest map(TTVaccine ttVaccine) {
        final LocalDate vaccinationDate = ttVaccine.getVaccinationDate().toLocalDate();
        return new EnrollmentRequest(ttVaccine.getPatient().getMRSPatientId(), ScheduleNames.TT_VACCINATION,
                new Time(DateUtil.now().toLocalTime()), vaccinationDate, null, vaccinationDate, null, ttVaccine.getDosage().getScheduleMilestoneName(), null);
    }
}
