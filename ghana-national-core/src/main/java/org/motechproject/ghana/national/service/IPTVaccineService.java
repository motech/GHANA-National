package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.mapper.IPTVaccineEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;


@Service
public class IPTVaccineService {

    @Autowired
    StaffService staffService;

    @Autowired
    ANCVisitService ancVisitService;

    @Autowired
    AllEncounters allEncounters;

    @Autowired
    ScheduleTrackingService scheduleTrackingService;

    public void enrollForPregnancyIPT(Patient patient, LocalDate expectedDateOfDelivery) {
        Pregnancy pregnancy = basedOnDeliveryDate(expectedDateOfDelivery);
        int currentWeek = pregnancy.currentWeek();
        if (currentWeek > 0 && currentWeek <= 13) {
            EnrollmentRequest enrollmentRequest = new IPTVaccineEnrollmentMapper().mapIPTp(patient, pregnancy.dateOfConception());
            scheduleTrackingService.enroll(enrollmentRequest);
        }
    }
}
