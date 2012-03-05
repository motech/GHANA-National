package org.motechproject.ghana.national.tools.seed.data.domain;

import java.util.List;

public class OldGhanaFacility {
    private String name;
    private String id;

    public OldGhanaFacility(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public OldGhanaFacility() {
    }

    public static OldGhanaFacility findByName(List<OldGhanaFacility> oldGhanaFacilities, String facilityName) {
        for (OldGhanaFacility oldGhanaFacility : oldGhanaFacilities) {
            if (facilityName.equals(oldGhanaFacility.getName())) {
                return oldGhanaFacility;
            }
        }
        return null;
    }
}