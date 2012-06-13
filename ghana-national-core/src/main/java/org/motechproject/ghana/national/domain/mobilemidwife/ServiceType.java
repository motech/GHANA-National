package org.motechproject.ghana.national.domain.mobilemidwife;

import java.util.ArrayList;
import java.util.List;

public enum ServiceType {
    PREGNANCY_TEXT("Pregnancy"),
    CHILD_CARE_TEXT("Child Care"),
    PREGNANCY_VOICE("Pregnancy"),
    CHILD_CARE_VOICE("Child Care");

    private String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getValue() {
        return name();
    }

    public String getServiceName(Medium medium) {
        return displayName + (Medium.SMS.equals(medium) ? " Text Message" : " Voice Message");
    }

    public String getDisplayName() {
        return displayName;
    }

    public static List<ServiceType> displayValues() {
        return new ArrayList<ServiceType>() {{
            add(ServiceType.PREGNANCY_TEXT);
            add(ServiceType.CHILD_CARE_TEXT);
        }};
    }

    public static ServiceType getServiceType(String serviceType, Medium medium) {
        if (Medium.SMS.equals(medium)) {
            if (serviceType.equals(ServiceType.CHILD_CARE_TEXT.getDisplayName())) {
                return ServiceType.CHILD_CARE_TEXT;
            }
            return ServiceType.PREGNANCY_TEXT;
        }
        if (Medium.VOICE.equals(medium)) {
            if (serviceType.equals(ServiceType.CHILD_CARE_VOICE.getDisplayName())) {
                return ServiceType.CHILD_CARE_VOICE;
            }
            return ServiceType.PREGNANCY_VOICE;
        }
        return null;
    }
}


