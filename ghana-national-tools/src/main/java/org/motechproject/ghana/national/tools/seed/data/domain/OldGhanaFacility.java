package org.motechproject.ghana.national.tools.seed.data.domain;

import java.util.List;

public class OldGhanaFacility {
    private String name;
    private String id;
    private String phoneNumber;
    private String additionalPhoneNumber1;
    private String additionalPhoneNumber2;
    private String additionalPhoneNumber3;

    public OldGhanaFacility(String name, String id, String phoneNumber, String additionalPhoneNumber1, String additionalPhoneNumber2, String additionalPhoneNumber3) {
        this.name = name;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.additionalPhoneNumber1 = additionalPhoneNumber1;
        this.additionalPhoneNumber2 = additionalPhoneNumber2;
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAdditionalPhoneNumber1() {
        return additionalPhoneNumber1;
    }

    public String getAdditionalPhoneNumber2() {
        return additionalPhoneNumber2;
    }

    public String getAdditionalPhoneNumber3() {
        return additionalPhoneNumber3;
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