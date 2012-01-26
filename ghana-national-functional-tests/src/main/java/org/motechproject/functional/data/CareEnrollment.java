package org.motechproject.functional.data;

import java.util.Map;

public interface CareEnrollment {

    Map<String, String> forClientRegistrationThroughMobile(TestPatient patient);
}
