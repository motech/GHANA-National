package org.motechproject.ghana.national.domain.mobilemidwife;

import org.motechproject.ghana.national.domain.Displayable;

public enum LearnedFrom implements Displayable {
    GHS_NURSE("GHS Nurse"),
    MOTECH_FIELD_AGENT("MoTeCH field agent"),
    FRIEND("Friend"),
    POSTERS_ADS("Posters/ads"),
    RADIO("Radio");

    private String displayName;

    LearnedFrom(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public String value(){
        return name();
    }

}
