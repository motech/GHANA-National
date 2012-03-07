package org.motechproject.ghana.national.mapper;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static org.motechproject.util.DateUtil.today;

public class TTVaccinationEnrollmentMapper {
    public EnrollmentRequest map(TTVaccine ttVaccine) {
        return new EnrollmentRequest(ttVaccine.getPatient().getMRSPatientId(), ScheduleNames.TT_VACCINATION_VISIT,
                new Time(DateUtil.now().toLocalTime()), ttVaccine.getVaccinationDate().toLocalDate(), null, today(), null, ttVaccine.getDosage().getScheduleMilestoneName());
    }
}
