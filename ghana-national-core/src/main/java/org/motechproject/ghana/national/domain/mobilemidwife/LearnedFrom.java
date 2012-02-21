package org.motechproject.ghana.national.domain.mobilemidwife;

public enum LearnedFrom{
    GHS_NURSE("GHS Nurse"),
    MOTECH_FIELD_AGENT("MoTeCH field agent"),
    FRIEND("Friend"),
    POSTERS_ADS("Posters/ads"),
    FAMILY_MEMBER("family member"),
    RADIO("Radio");

    private String displayName;

    LearnedFrom(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue(){
        return name();
    }

}
