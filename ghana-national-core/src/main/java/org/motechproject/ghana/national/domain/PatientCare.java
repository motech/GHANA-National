package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.util.DateUtil.time;

public class PatientCare {
    private String name;
    private LocalDate enrollmentDate;
    private Time enrollmentTime;
    private Time referenceTime;
    private LocalDate referenceDate;
    private String milestoneName;
    private Map<String, String> metaData = new HashMap<String, String>();

    public PatientCare(String name, LocalDate referenceDate, LocalDate enrollmentDate, String milestoneName, Map<String, String> metaData) {
        this.name = name;
        this.referenceDate = referenceDate;
        this.enrollmentDate = enrollmentDate;
        this.milestoneName= milestoneName;
        this.metaData = metaData;

    }

    public PatientCare(String name, DateTime referenceDateTime, DateTime enrollmentDateTime, String milestoneName, Map<String, String> metaData) {
        this(name, referenceDateTime.toLocalDate(), enrollmentDateTime.toLocalDate(), milestoneName, metaData);
        this.referenceTime = time(referenceDateTime);
        this.enrollmentTime = time(enrollmentDateTime);
    }

    public String name() {
        return name;
    }

    public LocalDate startingOn() {
        return referenceDate;
    }

    public Time referenceTime() {
        return referenceTime;
    }

    public Time preferredTime() {
        return  defaultPreferredTimeIfNoReferenceTimeIsSet();
    }

    private Time defaultPreferredTimeIfNoReferenceTimeIsSet() {
        return referenceTime == null ? time(DateUtil.now()) : null;
    }

    public LocalDate enrollmentDate() {
        return enrollmentDate;  
    }

    public Time enrollmentTime() {
        return enrollmentTime;
    }

    public Map<String, String> metaData() {
        return metaData;
    }

    public String milestoneName() {
        return milestoneName;
    }

    public PatientCare milestoneName(String milestoneName){
        this.milestoneName=milestoneName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientCare that = (PatientCare) o;

        if (!enrollmentDate.equals(that.enrollmentDate)) return false;
        if (enrollmentTime != null ? !enrollmentTime.equals(that.enrollmentTime) : that.enrollmentTime != null)
            return false;
        if (metaData != null ? !metaData.equals(that.metaData) : that.metaData != null) return false;
        if (!name.equals(that.name)) return false;
        if (!referenceDate.equals(that.referenceDate)) return false;
        if (referenceTime != null ? !referenceTime.equals(that.referenceTime) : that.referenceTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + enrollmentDate.hashCode();
        result = 31 * result + (enrollmentTime != null ? enrollmentTime.hashCode() : 0);
        result = 31 * result + (referenceTime != null ? referenceTime.hashCode() : 0);
        result = 31 * result + referenceDate.hashCode();
        result = 31 * result + (metaData != null ? metaData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PatientCare{" +
                "name='" + name + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", enrollmentTime=" + enrollmentTime +
                ", referenceTime=" + referenceTime +
                ", referenceDate=" + referenceDate +
                ", metaData=" + metaData +
                '}';
    }
}
