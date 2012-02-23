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
}
