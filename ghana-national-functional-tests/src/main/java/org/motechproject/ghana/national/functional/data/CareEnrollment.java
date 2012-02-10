package org.motechproject.ghana.national.functional.data;

import java.util.Map;

public interface CareEnrollment {

    Map<String, String> forClientRegistrationThroughMobile(TestPatient patient);

    Map<String, String> withMobileMidwifeEnrollmentThroughMobile(TestMobileMidwifeEnrollment mmEnrollmentDetails);

    Map<String, String> withoutMobileMidwifeEnrollmentThroughMobile();

}
