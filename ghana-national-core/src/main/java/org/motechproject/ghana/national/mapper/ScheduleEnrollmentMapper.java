package org.motechproject.ghana.national.mapper;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static org.motechproject.ghana.national.configuration.ScheduleNames.*;

public class ScheduleEnrollmentMapper {
    public EnrollmentRequest mapForDelivery(Patient patient, LocalDate expectedDeliveryDate) {
        return new EnrollmentRequest(patient.getMRSPatientId(), DELIVERY,
                new Time(DateUtil.now().toLocalTime()), Pregnancy.basedOnDeliveryDate(expectedDeliveryDate).dateOfConception());
    }

    public EnrollmentRequest mapForIPTp(Patient patient, LocalDate dateOfConception) {
        return new EnrollmentRequest(patient.getMRSPatientId(), ANC_IPT_VACCINE,
                new Time(DateUtil.now().toLocalTime()), dateOfConception);
    }
}
