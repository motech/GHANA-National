package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentService;
import org.motechproject.ghana.national.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllAppointments {
    @Autowired
    AppointmentService appointmentService;

    public void remove(Patient patient) {
        appointmentService.removeCalendar(patient.getMotechId());
    }

    public void updateANCVisitSchedule(Patient patient, DateTime dueDate) {
//        remove(patient);
//        final ANCVisitAppointment ancVisitAppointment = ANCVisitAppointment.createFor(dueDate);
//        AppointmentCalendarRequest calendarRequest = new AppointmentCalendarRequest();
//        calendarRequest.setExternalId(patient.getMotechId());
//        appointmentService.addCalendar(calendarRequest);
//
//        final ReminderConfiguration configuration = new ReminderConfiguration();
//        configuration.setIntervalUnit(ReminderConfiguration.IntervalUnit.valueOf(ancVisitAppointment.getReminderInterval()));
//        configuration.setIntervalCount(ancVisitAppointment.getReminderCount());
//        appointmentService.addVisit(patient.getMotechId(), "DueVisit - " + patient.getMotechId(), new VisitRequest() {{
//            setDueDate(ancVisitAppointment.getDue());
//            setReminderConfiguration(configuration);
//        }});
//        appointmentService.addVisit(patient.getMotechId(), "LateVisit - " + patient.getMotechId(), new VisitRequest() {{
//            setDueDate(ancVisitAppointment.getLate());
//            setReminderConfiguration(configuration);
//        }});
//        appointmentService.addVisit(patient.getMotechId(), "MaxVisit - " + patient.getMotechId(), new VisitRequest() {{
//            setDueDate(ancVisitAppointment.getMax());
//            setReminderConfiguration(configuration);
//        }});
    }
}
