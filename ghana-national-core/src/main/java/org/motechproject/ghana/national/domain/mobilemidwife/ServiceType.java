package org.motechproject.ghana.national.domain.mobilemidwife;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum ServiceType{
    PREGNANCY("Pregnancy"){public Map<String, String> messageStartWeeks(){
        HashMap<String, String> messageStartWeeks = new LinkedHashMap<String, String>();
        for (int i = 5; i < 41; i++) {
            messageStartWeeks.put(String.valueOf(i), "Pregnancy-week " + i);
        }
        return messageStartWeeks;
    }},
    CHILD_CARE("Child Care"){public Map<String, String> messageStartWeeks(){
        HashMap<String, String> messageStartWeeks = new LinkedHashMap<String, String>();
        for (int i = 1; i < 53; i++) {
            messageStartWeeks.put(String.valueOf(40 + i), "Baby-week " + i);
        }
        return messageStartWeeks;
    }};

    private String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue(){
        return name();
    }

    public abstract Map<String, String> messageStartWeeks();
}


