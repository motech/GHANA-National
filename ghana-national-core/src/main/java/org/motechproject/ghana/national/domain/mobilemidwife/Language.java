package org.motechproject.ghana.national.domain.mobilemidwife;

import org.motechproject.ghana.national.domain.Displayable;

public enum Language implements Displayable{
    EN("English"), KAS("Kassim"), NAN("Nankam"), FAN("Fante");
    private String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
    public String value() {
        return name();
    }
}


