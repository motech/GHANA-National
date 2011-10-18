package org.ghana.national.web.form;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.services.Facility;

import java.util.List;

public class CreateFacilityForm {
    private String name;
    private String country;
    private String region;
    private String countyDistrict;
    private String stateProvince;

    public CreateFacilityForm() {
    }

    public CreateFacilityForm(String name, String country, String region, String countyDistrict, String stateProvince) {
        this.name = name;
        this.country = country;
        this.region = region;
        this.countyDistrict = countyDistrict;
        this.stateProvince = stateProvince;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountyDistrict() {
        return countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }


    public boolean isDuplicate(Facility facility) {
        return (facility != null
                && StringUtils.equals(facility.getName(), this.getName())
                && StringUtils.equals(facility.getStateProvince(), this.getStateProvince())
                && StringUtils.equals(facility.getCountyDistrict(), this.getCountyDistrict())
                && StringUtils.equals(facility.getRegion(), this.getRegion())
                && StringUtils.equals(facility.getCountry(), this.getCountry()));
    }

    public boolean isIn(List<Facility> facilities) {
        for (Facility facility : facilities) {
            if (this.isDuplicate(facility)) {
                return true;
            }
        }
        return false;
    }
}
