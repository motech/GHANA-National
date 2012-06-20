package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.ghana.national.domain.AlertWindow;

public enum AudioPrompts {
    LANGUAGE_PROMPT("prompt_A"), OPTIONS_PROMPT("prompt_B"), MOTECHID_PROMPT("prompt_C"), UNEXPECTED_ERROR("UNEXPECTED_ERROR");

    private String message;

    AudioPrompts(String message) {
        this.message = message;
    }

    public String value() {
        return message;
    }

    public static String fileNameForCareSchedule(String scheduleName, AlertWindow alertWindow) {
        return "prompt_" + scheduleName + "_" + alertWindow.getName();
    }

    public static String fileNameForMobileMidwife(String programName, String week) {
        return "prompt_" + programName + "_" + week;
    }
}
