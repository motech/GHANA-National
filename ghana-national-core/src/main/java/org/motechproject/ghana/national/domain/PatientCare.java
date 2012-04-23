package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;

public class PatientCare {
    private String name;
    private LocalDate referenceDate;

    public PatientCare(String name, LocalDate referenceDate) {
        this.referenceDate = referenceDate;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public LocalDate startingOn() {
        return referenceDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientCare that = (PatientCare) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (referenceDate != null ? !referenceDate.equals(that.referenceDate) : that.referenceDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (referenceDate != null ? referenceDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + ": " + referenceDate;
    }
}
