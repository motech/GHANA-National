package org.motechproject.ghana.national.domain;

import org.junit.Test;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

public class SMSTemplateTest {

    @Test
    public void shouldFillPatientDetails(){
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String patientMotechId = "MotechId";
        String facilityId = "facilityId";
        final String gender = "M";

        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastName).dateOfBirth(DateUtil.newDate(2000, 1, 1).toDate()).age(30).gender(gender);
        final Patient patient = new Patient(new MRSPatient(patientMotechId, person, new MRSFacility(facilityId)));

        final HashMap<String,String> runtimeVariables = new SMSTemplate().fillPatientDetails(patient).getRuntimeVariables();

        assertContainsTemplateValues(new HashMap<String, String>(){{
            put(MOTECH_ID, patientMotechId);
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastName);
            put(GENDER, gender);
            put(DOB, "01 Jan 2000");
            put(AGE, "30");
            put(PHONE_NUMBER, "");
        }}, runtimeVariables);
    }

    public static void assertContainsTemplateValues(Map<String, String> expectedValues, Map<String, String> actualValues) {
        for (Map.Entry<String, String> entry : expectedValues.entrySet()) {
            assertThat("Failed : expected value : " + entry + " actual value : " + actualValues.get(entry.getKey()),
                    actualValues.get(entry.getKey()), is(equalTo(entry.getValue())));
        }
    }
}
