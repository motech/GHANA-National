package org.motechproject.ghana.national.mapper;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import static org.motechproject.ghana.national.configuration.ScheduleNames.DELIVERY;

public class ScheduleEnrollmentMapper {
    public EnrollmentRequest mapForDelivery(Patient patient, LocalDate expectedDeliveryDate) {
        return new EnrollmentRequest(patient.getMRSPatientId(), DELIVERY,
                new Time(DateUtil.now().toLocalTime()), Pregnancy.basedOnDeliveryDate(expectedDeliveryDate).dateOfConception());
    }
}
