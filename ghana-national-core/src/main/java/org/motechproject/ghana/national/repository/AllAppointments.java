package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentService;
import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.ghana.national.domain.EncounterType;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AllAppointments {

    @Autowired
    AppointmentService appointmentService;

    public void remove(Patient patient) {
        appointmentService.removeCalendar(patient.getMotechId());
    }

    public void updateANCVisitSchedule(Patient patient, Date visitedDate, DateTime nextVisitDate) {
        appointmentService.visited(patient.getMotechId(), EncounterType.ANC_VISIT.value(), DateUtil.newDateTime(visitedDate));

        CreateVisitRequest createVisitRequest = new CreateVisitRequest().setAppointmentDueDate(nextVisitDate).setVisitName(EncounterType.ANC_VISIT.value());

        createVisitRequest.addAppointmentReminderConfiguration(new ReminderConfiguration().setRemindFrom(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.SECONDS));
        createVisitRequest.addAppointmentReminderConfiguration(new ReminderConfiguration().setRemindFrom(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.WEEKS).setIntervalCount(1));
        createVisitRequest.addAppointmentReminderConfiguration(new ReminderConfiguration().setRemindFrom(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.WEEKS).setIntervalCount(3));

        appointmentService.addVisit(patient.getMotechId(), createVisitRequest);
    }
}
