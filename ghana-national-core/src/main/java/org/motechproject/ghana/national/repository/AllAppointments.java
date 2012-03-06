package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.AppointmentService;
import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AllAppointments {
    @Autowired
    AppointmentService appointmentService;


    public void remove(Patient patient) {
        appointmentService.removeCalendar(patient.getMotechId());
    }

    public void updateANCVisitSchedule(Patient patient, Date visitedDate, final DateTime nextVisitDate) {
        appointmentService.visited(patient.getMotechId(), "VISIT NAME!!!!", DateUtil.newDateTime(visitedDate));
        CreateVisitRequest createVisitRequest = new CreateVisitRequest();

        Map<String, Object> data = new HashMap<String, Object>() {{
            put("visitName", "VISIT NAME!!!!");
            put("typeOfVisit", "TYPE");
            put("appointmentDueDate", nextVisitDate);
        }};
        appointmentService.addVisit(patient.getMotechId(), createVisitRequest.setData(data));
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
