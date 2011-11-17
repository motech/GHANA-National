package org.motechproject.ghana.national.service;

import org.motechproject.openmrs.util.VerhoeffValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifierService {
    private IdentifierGenerationService identifierGenerationService;
    private VerhoeffValidator validator;

    @Autowired
    public IdentifierService(IdentifierGenerationService identifierGenerationService) {
        this.identifierGenerationService = identifierGenerationService;
    }

    public String newPatientId() {
        return decorateId(identifierGenerationService.newPatientId());
    }

    public String newStaffId() {
        return decorateId(identifierGenerationService.newStaffId());
    }

    public String newFacilityId() {
        return decorateId(identifierGenerationService.newFacilityId());
    }

    private String decorateId(String identifier) {
        return validator.getValidIdentifier(identifier);
    }
}
