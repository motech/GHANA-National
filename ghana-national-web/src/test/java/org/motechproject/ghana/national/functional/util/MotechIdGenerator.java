package org.motechproject.ghana.national.functional.util;

import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MotechIdGenerator {

    @Autowired
    private IdentifierGenerationService identifierGenerationService;

    public String generatePrePrintedPatientId(){
        return identifierGenerationService.newPatientId();
    }
}
