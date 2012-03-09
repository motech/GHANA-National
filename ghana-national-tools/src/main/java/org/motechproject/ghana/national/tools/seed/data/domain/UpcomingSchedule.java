package org.motechproject.ghana.national.tools.seed.data.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.util.DateUtil;

import java.util.HashSet;
import java.util.List;

public class UpcomingSchedule {
    private String patientId;
    private String milestoneName;
    private String conceptId;
    private DateTime dueDatetime;
    private DateTime minDatetime;
    private DateTime lateDatetime;
    private DateTime maxDatetime;
    private String careName;
    private Boolean voided;

    public UpcomingSchedule(String patientId, String dueDatetime, String milestoneName, String conceptId, String careName, String minDatetime, String lateDatetime, String maxDatetime, Integer voided) {
        this.patientId = patientId;
        this.conceptId = conceptId;
        this.careName = careName;
        this.minDatetime = parse(minDatetime);
        this.lateDatetime = parse(lateDatetime);
        this.maxDatetime = parse(maxDatetime);
        this.dueDatetime = parse(dueDatetime);
        this.milestoneName = milestoneName;
        this.voided = voided == 1;
    }

    public static boolean areDuplicates(List<UpcomingSchedule> upcomingSchedules) {
        return new HashSet<UpcomingSchedule>(upcomingSchedules).size() == 1;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpcomingSchedule)) return false;

        UpcomingSchedule that = (UpcomingSchedule) o;

        if (careName != null ? !careName.equals(that.careName) : that.careName != null) return false;
        if (conceptId != null ? !conceptId.equals(that.conceptId) : that.conceptId != null) return false;
        if (dueDatetime != null ? !dueDatetime.equals(that.dueDatetime) : that.dueDatetime != null) return false;
        if (lateDatetime != null ? !lateDatetime.equals(that.lateDatetime) : that.lateDatetime != null) return false;
        if (maxDatetime != null ? !maxDatetime.equals(that.maxDatetime) : that.maxDatetime != null) return false;
        if (milestoneName != null ? !milestoneName.equals(that.milestoneName) : that.milestoneName != null)
            return false;
        if (minDatetime != null ? !minDatetime.equals(that.minDatetime) : that.minDatetime != null) return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dueDatetime != null ? dueDatetime.hashCode() : 0;
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        result = 31 * result + (milestoneName != null ? milestoneName.hashCode() : 0);
        result = 31 * result + (conceptId != null ? conceptId.hashCode() : 0);
        result = 31 * result + (careName != null ? careName.hashCode() : 0);
        result = 31 * result + (minDatetime != null ? minDatetime.hashCode() : 0);
        result = 31 * result + (lateDatetime != null ? lateDatetime.hashCode() : 0);
        result = 31 * result + (maxDatetime != null ? maxDatetime.hashCode() : 0);
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
}
