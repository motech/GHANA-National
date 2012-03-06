package org.motechproject.ghana.national.tools.seed.data.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.util.DateUtil;

public class UpcomingSchedule {
    private String patientId;
    private String milestoneName;
    private String type;
    private String motechId;
    private DateTime dueDatetime;
    private DateTime minDatetime;
    private DateTime lateDatetime;
    private DateTime maxDatetime;
    private Boolean voided;

    public UpcomingSchedule(String patientId, String dueDatetime, String milestoneName, String type, String minDatetime,
                            String lateDatetime, String maxDatetime, Integer voided, String motechId) {
        this.patientId = patientId;
        this.type = type;
        this.motechId = motechId;
        this.minDatetime = parse(minDatetime);
        this.lateDatetime = parse(lateDatetime);
        this.maxDatetime = parse(maxDatetime);
        this.dueDatetime = parse(dueDatetime);
        this.milestoneName = milestoneName;
        this.voided = (voided == 1);
    }

    private DateTime parse(String minDatetime) {
        return minDatetime != null ?
                DateUtil.newDateTime(DateTime.parse(minDatetime, DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.S")).toDate())
                : null;
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

    public DateTime getLateDatetime() {
        return lateDatetime;
    }

    public DateTime getMaxDatetime() {
        return maxDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpcomingSchedule)) return false;

        UpcomingSchedule schedule = (UpcomingSchedule) o;

        if (dueDatetime != null ? !dueDatetime.equals(schedule.dueDatetime) : schedule.dueDatetime != null)
            return false;
        if (lateDatetime != null ? !lateDatetime.equals(schedule.lateDatetime) : schedule.lateDatetime != null)
            return false;
        if (maxDatetime != null ? !maxDatetime.equals(schedule.maxDatetime) : schedule.maxDatetime != null)
            return false;
        if (milestoneName != null ? !milestoneName.equals(schedule.milestoneName) : schedule.milestoneName != null)
            return false;
        if (minDatetime != null ? !minDatetime.equals(schedule.minDatetime) : schedule.minDatetime != null)
            return false;
        if (motechId != null ? !motechId.equals(schedule.motechId) : schedule.motechId != null) return false;
        if (patientId != null ? !patientId.equals(schedule.patientId) : schedule.patientId != null) return false;
        if (type != null ? !type.equals(schedule.type) : schedule.type != null) return false;
        if (voided != null ? !voided.equals(schedule.voided) : schedule.voided != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = patientId != null ? patientId.hashCode() : 0;
        result = 31 * result + (milestoneName != null ? milestoneName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (motechId != null ? motechId.hashCode() : 0);
        result = 31 * result + (dueDatetime != null ? dueDatetime.hashCode() : 0);
        result = 31 * result + (minDatetime != null ? minDatetime.hashCode() : 0);
        result = 31 * result + (lateDatetime != null ? lateDatetime.hashCode() : 0);
        result = 31 * result + (maxDatetime != null ? maxDatetime.hashCode() : 0);
        result = 31 * result + (voided != null ? voided.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpcomingSchedule{" +
                "dueDatetime=" + dueDatetime +
                ", patientId='" + patientId + '\'' +
                ", mileStoneName='" + milestoneName + '\'' +
                '}';
    }

    public Boolean getVoided() {
        return voided;
    }

    public String getMotechId() {
        return motechId;
    }
}
