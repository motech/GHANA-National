package org.motechproject.ghana.national.domain.mobilemidwife;

import org.motechproject.ghana.national.domain.Displayable;

public enum Medium implements Displayable{
    SMS("SMS"), VOICE("Voice");
    private String displayName;

    Medium(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public String value(){
        return name();
    }

}
