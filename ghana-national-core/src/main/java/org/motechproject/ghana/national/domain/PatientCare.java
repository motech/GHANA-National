package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class PatientCare {
    private String name;
    private Time referenceTime;
    private LocalDate referenceDate;

    public PatientCare(String name, LocalDate referenceDate) {
        this.referenceDate = referenceDate;
        this.name = name;
    }

    public PatientCare(String name, LocalDate referenceDate, Time referenceTime) {
        this.referenceDate = referenceDate;
        this.name = name;
        this.referenceTime = referenceTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientCare that = (PatientCare) o;

        if (!name.equals(that.name)) return false;
        if (!referenceDate.equals(that.referenceDate)) return false;
        if (referenceTime != null ? !referenceTime.equals(that.referenceTime) : that.referenceTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (referenceTime != null ? referenceTime.hashCode() : 0);
        result = 31 * result + referenceDate.hashCode();
        return result;
    }
}
