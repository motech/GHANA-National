package org.motechproject.ghana.national.functional.helper;

import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdGenHelper {

    @Autowired
    private IdentifierGenerationService identifierGenerationService;

    public String generatePrePrintedPatientId(){
        return identifierGenerationService.newPatientId();
    }
}
