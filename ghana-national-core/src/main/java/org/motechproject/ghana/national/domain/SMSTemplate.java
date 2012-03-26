package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.HashMap;

import static java.lang.String.format;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.Constants.PATTERN_DD_MMM_YYYY;
import static org.motechproject.ghana.national.tools.Utility.*;
import static org.motechproject.ghana.national.tools.Utility.nullSafeToString;

public class SMSTemplate {

    private HashMap<String, String> runtimeVariables;

    public SMSTemplate() {
        runtimeVariables = new HashMap<String, String>();
    }

    public SMSTemplate fillPatientDetails(Patient patient) {
        runtimeVariables.put(MOTECH_ID, patient.getMotechId());
        runtimeVariables.put(FIRST_NAME, patient.getFirstName());
        runtimeVariables.put(LAST_NAME, patient.getLastName());
        runtimeVariables.put(GENDER, patient.getGender());
        runtimeVariables.put(DOB, nullSafeToString(patient.dateOfBirth(), PATTERN_DD_MMM_YYYY));
        runtimeVariables.put(AGE, nullSafe(safeToString(patient.getAge()), ""));
        runtimeVariables.put(PHONE_NUMBER, nullSafe(patient.getPhoneNumber(), ""));
        return this;
    }

    public SMSTemplate fillEDD(LocalDate edd){
        runtimeVariables.put(DATE, nullSafeToString(edd, PATTERN_DD_MMM_YYYY));
        return this;
    }

    public SMSTemplate fillScheduleDetails(String milestoneName, String windowName){
        runtimeVariables.put(WINDOW, windowName);
        runtimeVariables.put(MILESTONE_NAME, milestoneName);
        return this;
    }
    
    public SMSTemplate fillCareSchedulesDueDate(String milestoneName, DateTime startOfDueWindow){
        runtimeVariables.put(MILESTONE_NAME, milestoneName);
        runtimeVariables.put(DATE, nullSafeToString(startOfDueWindow, PATTERN_DD_MMM_YYYY));
        return this;
    }

    public SMSTemplate fillFacilityDetails(Patient patient){
        runtimeVariables.put(FACILITY, patient.getMrsPatient().getFacility().getName());
        return this;
    }

    public HashMap<String, String> getRuntimeVariables() {
        return runtimeVariables;
    }

    public SMSTemplate fillDeliveryTime(DateTime deliveryTime) {
        runtimeVariables.put(DELIVERY_TIME, nullSafeToString(deliveryTime, PATTERN_DD_MMM_YYYY));
        return this;
    }

    public SMSTemplate fillTemplate(String templateName, String value) {
        runtimeVariables.put(template(templateName), value);
        return this;
    }

    private String template(String templateName) {
        return format(TEMPLATE, templateName);
    }
}
