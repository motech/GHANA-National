package org.motechproject.ghana.national.domain;

import org.motechproject.scheduletracking.api.domain.WindowName;

public enum AlertWindow {
    UPCOMING("Upcoming", WindowName.earliest.name(), 1),
    DUE("Due", WindowName.due.name(), 2),
    OVERDUE("Overdue", WindowName.late.name(), 3);

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

    public static AlertWindow byPlatformName(String platformWindowName){
        for (AlertWindow alertWindow : values()) {
            if (platformWindowName.equals(alertWindow.platformWindowName))
                return alertWindow;
        }
        return null;
    }

    public Integer getOrder() {
        return order;
    }
}
