package org.motechproject.ghana.national.domain;

import org.motechproject.scheduletracking.api.domain.WindowName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AlertWindow implements Serializable {
    UPCOMING("Upcoming", WindowName.earliest.name(), 1),
    DUE("Due", WindowName.due.name(), 2),
    OVERDUE("Overdue", WindowName.late.name(), 3),
    MAX("Final Overdue", WindowName.max.name(), 4);

    private String name;
    private String platformWindowName;
    private Integer order;

    AlertWindow(String name, String platformWindowName, Integer order) {
        this.name = name;
        this.platformWindowName = platformWindowName;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public static AlertWindow byName(String name){
        for (AlertWindow alertWindow : values()) {
            if (alertWindow.name.equals(name))
                return alertWindow;
        }
        return null;
    }

    public static AlertWindow byPlatformName(String platformWindowName) {
        for (AlertWindow alertWindow : values()) {
            if (alertWindow.platformWindowName.equals(platformWindowName))
                return alertWindow;
        }
        return null;
    }

    public Integer getOrder() {
        return order;
    }

    public String getPlatformWindowName() {
        return platformWindowName;
    }

    public static List<String> ghanaNationalWindowNames() {
        List<String> names = new ArrayList<String>();
        for (AlertWindow alertWindow : Arrays.asList(UPCOMING, DUE, OVERDUE)) {
            names.add(alertWindow.getName());
        }
        return names;
    }
}
