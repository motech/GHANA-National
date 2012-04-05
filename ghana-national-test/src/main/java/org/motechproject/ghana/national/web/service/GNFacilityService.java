package org.motechproject.ghana.national.web.service;

import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GNFacilityService {

    @Autowired
    private FacilityService facilityService;

    public List<Facility> getAFacility() {
        return facilityService.searchFacilities(null, "Ashanti", "Ghana", "Ashanti", null, null);

    }
}