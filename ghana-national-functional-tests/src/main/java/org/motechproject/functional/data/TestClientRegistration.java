package org.motechproject.functional.data;


import java.util.HashMap;
import java.util.Map;

public class TestClientRegistration<T extends CareEnrollment>{

    private TestPatient patient;

    private T enrollment;

    public TestClientRegistration(TestPatient patient, T enrollment) {
        this.patient = patient;
        this.enrollment = enrollment;
    }

    public Map<String, String> forMobile() {
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(patient.forMobile());
        map.putAll(enrollment.forClientRegistrationThroughMobile(patient));
        return map;
    }

    public T getEnrollment() {
        return enrollment;
    }

    public TestPatient getPatient() {
        return patient;
    }
}
