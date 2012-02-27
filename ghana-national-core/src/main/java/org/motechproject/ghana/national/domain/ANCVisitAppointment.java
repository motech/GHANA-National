package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;

public class ANCVisitAppointment {
    String reminderInterval;
    int reminderCount;
    DateTime due;
    DateTime late;
    DateTime max;
    DateTime dueDate;

    private ANCVisitAppointment() {
    }

    public static ANCVisitAppointment createFor(DateTime dueDate) {
        ANCVisitAppointment appointment = new ANCVisitAppointment();
        appointment.dueDate = dueDate;
        appointment.reminderInterval = "WEEKS";
        appointment.reminderCount = 1;
        appointment.due = dueDate;
        appointment.late = dueDate.plusWeeks(1);
        appointment.max = dueDate.plusWeeks(3);
        return appointment;
    }

    public String getReminderInterval() {
        return reminderInterval;
    }

    public int getReminderCount() {
        return reminderCount;
    }

    public DateTime getDue() {
        return due;
    }

    public DateTime getLate() {
        return late;
    }

    public DateTime getMax() {
        return max;
    }
}
