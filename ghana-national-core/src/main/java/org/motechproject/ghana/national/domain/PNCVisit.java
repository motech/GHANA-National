package org.motechproject.ghana.national.domain;

public enum PNCVisit {

    PNC1(1, "PNC-BABY-1"), PNC2(2, "PNC-BABY-2"), PNC3(3, "PNC-BABY-3");

    private int visitNo;
    private String scheduleName;

    PNCVisit(int visitNo, String scheduleName) {
        this.visitNo = visitNo;
        this.scheduleName = scheduleName;
    }

    public int visitNumber() {
        return visitNo;
    }

    public static PNCVisit byVisitNumber(Integer visitNumber) {
        for (PNCVisit pncVisit : values()) {
            if (visitNumber == pncVisit.visitNumber()) return pncVisit;
        }
        throw new IllegalArgumentException("pncvisit not valid : " + visitNumber);
    }

    public String scheduleName() {
        return scheduleName;
    }
}
