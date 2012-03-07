package org.motechproject.ghana.national.tools.seed.data.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.util.DateUtil;

public class UpcomingSchedule {
    private DateTime dueDatetime;
    private String patientId;
    private String milestoneName;

    public UpcomingSchedule(String patientId, String dueDatetime, String milestoneName) {
        this.patientId = patientId;
        this.dueDatetime = DateUtil.newDateTime(DateTime.parse(dueDatetime, DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.S")).toDate());
        this.milestoneName = milestoneName;
    }

    public DateTime getDueDatetime() {
        return dueDatetime;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    @Override
    public String toString() {
        return "UpcomingSchedule{" +
                "dueDatetime=" + dueDatetime +
                ", patientId='" + patientId + '\'' +
                ", mileStoneName='" + milestoneName + '\'' +
                '}';
    }
}
