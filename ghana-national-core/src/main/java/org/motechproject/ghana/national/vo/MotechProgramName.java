package org.motechproject.ghana.national.vo;

public enum MotechProgramName {
    CWC("Weekly Info Child Message Program");
    private String programName;

    MotechProgramName(String programName) {
        this.programName = programName;
    }

    public String getName() {
        return programName;
    }
}
