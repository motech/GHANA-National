package org.motechproject.ghana.national.ivr;

import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Random;

public class MockIdentifierGenerator extends IdentifierGenerator {

    private long patientIdCounter = 777777;
    private long staffIdCounter = 7777;
    private Random random = new Random();

    public String newPatientId() {
        return new MotechIdVerhoeffValidator().getValidIdentifier(String.valueOf(random.nextInt(787878)));
    }

    public String newStaffId() {
        return new VerhoeffValidator().getValidIdentifier(String.valueOf(random.nextInt(7778)));
    }

    public String newFacilityId() {
        return null;
    }

}
