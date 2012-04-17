package org.motechproject.ghana.national.domain;

import java.util.ArrayList;
import java.util.List;

public enum PNCChildVisit {

    PNC1(1, "PNC-CHILD-1"), PNC2(2, "PNC-CHILD-2"), PNC3(3, "PNC-CHILD-3");

    private int visitNo;
    private String scheduleName;

    PNCChildVisit(int visitNo, String scheduleName) {
        this.visitNo = visitNo;
        this.scheduleName = scheduleName;
    }

    public int visitNumber() {
        return visitNo;
    }

    public static PNCChildVisit byVisitNumber(Integer visitNumber) {
        for (PNCChildVisit pncChildVisit : values()) {
            if (visitNumber == pncChildVisit.visitNumber()) return pncChildVisit;
        }
        throw new IllegalArgumentException("pncvisit not valid : " + visitNumber);
    }

    public static List<String> schedules() {
        List<String> schedules = new ArrayList<String>();
        for (PNCChildVisit visit : PNCChildVisit.values()) {
             schedules.add(visit.scheduleName());
        }
        return schedules;
    }

    public String scheduleName() {
        return scheduleName;
    }
}
