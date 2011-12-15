package org.motechproject.ghana.national.tools.seed.data.source;

import java.util.List;

public class FacilityNameAndId {
    private String name;
    private String id;

    public FacilityNameAndId(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public FacilityNameAndId() {
    }

    public static FacilityNameAndId findByName(List<FacilityNameAndId> facilities, String facilityName) {
        for (FacilityNameAndId facility : facilities) {
            if (facilityName.equals(facility.getName())) {
                return facility;
            }
        }
        return null;
    }
}