package org.motechproject.functional.data;


import java.util.HashMap;
import java.util.Map;

public class TestRegClient {

    TestPatient patient;
    TestANC  anc;

    public TestRegClient testPatient(TestPatient testPatient){
        this.patient=testPatient;
        return this;
    }


    public TestRegClient testANC(TestANC testANC) {
        this.anc=testANC;
        return this;
    }

    public Map<String, String> forANCReg(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(this.patient.forMobile());
        map.putAll(this.anc.forMobile());
        return map;

    }
}
