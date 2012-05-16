package org.motechproject.ghana.national.ivr.domain;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class PatientOutbox {
    public List<String> messages(String motechId) {
        return Arrays.asList("message for " + motechId);
    }
}
