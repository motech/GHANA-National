package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.ghana.national.domain.ANCVisitAppointment;
import org.motechproject.ghana.national.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllAppointments {
    @Autowired
    AppointmentService appointmentService;

    public void fulfilVisit(Patient patient) {
        appointmentService.removeCalendar(patient.getMotechId());
    }

    public void createANCVisitSchedule(Patient patient, DateTime dueDate) {
        ANCVisitAppointment ancVisitAppointment = ANCVisitAppointment.createFor(dueDate);
        AppointmentCalendarRequest calendarRequest = new AppointmentCalendarRequest();
        calendarRequest.setExternalId(patient.getMotechId());
        appointmentService.addCalendar(calendarRequest);

        ReminderConfiguration configuration = new ReminderConfiguration();
        configuration.setIntervalUnit(ReminderConfiguration.IntervalUnit.valueOf(ancVisitAppointment.getReminderInterval()));
        configuration.setIntervalCount(ancVisitAppointment.getReminderCount());
        appointmentService.addVisit(patient.getMotechId(), ancVisitAppointment.getDue(), configuration, TypeOfVisit.Scheduled);
        appointmentService.addVisit(patient.getMotechId(), ancVisitAppointment.getLate(), configuration, TypeOfVisit.Scheduled);
        appointmentService.addVisit(patient.getMotechId(), ancVisitAppointment.getMax(), configuration, TypeOfVisit.Scheduled);
    }
}
