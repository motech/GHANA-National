package org.motechproject.ghana.national.domain;

import static org.motechproject.ghana.national.configuration.ScheduleNames.*;

public enum PNCVisitNumber {
    PNC1(PNC_MOTHER_1.getName()), PNC2(PNC_MOTHER_2.getName()), PNC3(PNC_MOTHER_3.getName());

    private String scheduleName;

    PNCVisitNumber(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getScheduleName() {
        return scheduleName;
    }
}
