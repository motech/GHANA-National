package org.motechproject.ghana.national.ivr.repositories;

import org.motechproject.ghana.national.ivr.domain.CallSession;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class AllCallSessions {
    private Map<String, CallSession> callSessions = new HashMap<String, CallSession>();

    public void add(CallSession callSession){
        callSessions.put(callSession.getId(), callSession);
    }

}
