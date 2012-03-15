package org.motechproject.ghana.national.domain;

public enum PNCMotherVisit {

    PNC1(1, "PNC-MOTHER-1"), PNC2(2, "PNC-MOTHER-2"), PNC3(3, "PNC-MOTHER-3");

    private int visitNo;
    private String scheduleName;

    PNCMotherVisit(int visitNo, String scheduleName) {
        this.visitNo = visitNo;
        this.scheduleName = scheduleName;
    }

    public int visitNumber() {
        return visitNo;
    }

    public static PNCMotherVisit byVisitNumber(Integer visitNumber) {
        for (PNCMotherVisit pncChildVisit : values()) {
            if (visitNumber == pncChildVisit.visitNumber()) return pncChildVisit;
        }
        throw new IllegalArgumentException("pncvisit not valid : " + visitNumber);
    }

    public String scheduleName() {
        return scheduleName;
    }
}
