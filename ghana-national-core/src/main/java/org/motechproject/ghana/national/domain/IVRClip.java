package org.motechproject.ghana.national.domain;

public class IVRClip {

    public String name(String scheduleName, AlertWindow alertWindow) {
        return "prompt_" + scheduleName + "_" + alertWindow.getName();
    }
}
