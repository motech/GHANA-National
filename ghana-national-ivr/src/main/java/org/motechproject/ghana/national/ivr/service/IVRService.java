package org.motechproject.ghana.national.ivr.service;

import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;

public class IVRService {

    private VerboiceIVRService verboiceIVRService;

    @Autowired
    public IVRService(VerboiceIVRService verboiceIVRService) {
        this.verboiceIVRService = verboiceIVRService;
    }


}
