package org.motechproject.ghana.national.tools.seed.data.domain;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

public class UpcomingSchedule {
    private LocalDateTime dueDatetime;
    private String patientId;
    private String careName;

    public UpcomingSchedule(String patientId, String dueDatetime, String careName) {
        this.patientId = patientId;
        this.careName = careName;
        this.dueDatetime = LocalDateTime.parse(dueDatetime, DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"));
    }

    public LocalDateTime getDueDatetime() {
        return dueDatetime;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCareName() {
        return careName;
    }

    @Override
    public String toString() {
        return "UpcomingSchedule{" +
                "dueDatetime=" + dueDatetime +
                ", patientId='" + patientId + '\'' +
                ", careName='" + careName + '\'' +
                '}';
    }
}
