package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static org.motechproject.util.DateUtil.time;

public class PatientCare {
    private String name;
    private LocalDate enrollmentDate;
    private Time enrollmentTime;
    private Time referenceTime;
    private LocalDate referenceDate;

    public PatientCare(String name, LocalDate referenceDate, LocalDate enrollmentDate) {
        this.name = name;
        this.referenceDate = referenceDate;
        this.enrollmentDate = enrollmentDate;
    }

    public PatientCare(String name, DateTime referenceDateTime, DateTime enrollmentDateTime) {
        this(name, referenceDateTime.toLocalDate(), enrollmentDateTime.toLocalDate());
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
        return  defaultPreferredTime();
    }

    private Time defaultPreferredTime() {
        return referenceTime == null ? time(DateUtil.now()) : referenceTime;
    }

    public LocalDate enrollmentDate() {
        return enrollmentDate;  
    }

    public Time enrollmentTime() {
        return enrollmentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientCare that = (PatientCare) o;

        if (!enrollmentDate.equals(that.enrollmentDate)) return false;
        if (enrollmentTime != null ? !enrollmentTime.equals(that.enrollmentTime) : that.enrollmentTime != null)
            return false;
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
                '}';
    }
}
