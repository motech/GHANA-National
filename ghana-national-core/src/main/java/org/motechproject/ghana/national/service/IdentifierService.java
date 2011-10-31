package org.motechproject.ghana.national.service;


import org.motechproject.mrs.services.MRSIdentifierAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifierService {
    private MRSIdentifierAdaptor identifierAdaptor;

    public IdentifierService() {
    }

    @Autowired
    public IdentifierService(MRSIdentifierAdaptor identifierAdaptor) {
        this.identifierAdaptor = identifierAdaptor;
    }

    public MRSIdentifierAdaptor getIdentifierAdaptor() {
        return identifierAdaptor;
    }
}
