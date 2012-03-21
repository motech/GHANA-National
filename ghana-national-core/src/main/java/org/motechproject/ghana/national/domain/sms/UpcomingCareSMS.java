package org.motechproject.ghana.national.domain.sms;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMSTemplate;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.join;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.ghana.national.domain.Constants.SMS_LIST_SEPERATOR;

public class UpcomingCareSMS {

    public static final String UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY = "UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY";
    public static final String UPCOMING_CARE_DUE_CLIENT_QUERY = "UPCOMING_CARE_DUE_CLIENT_QUERY";
    public static final String NONE_UPCOMING = "NONE_UPCOMING";

    private SMSGateway smsGateway;
    private Patient patient;
    private List<EnrollmentRecord> upcomingEnrollments;

    public UpcomingCareSMS(SMSGateway smsGateway, Patient patient, List<EnrollmentRecord> upcomingEnrollments) {
        this.smsGateway = smsGateway;
        this.patient = patient;
        this.upcomingEnrollments = upcomingEnrollments;
    }

    public String smsText() {
        if(isEmpty(upcomingEnrollments)) {
            String nocaresTemplate = smsGateway.getSMSTemplate(NONE_UPCOMING);
            return SMS.fill(nocaresTemplate, new SMSTemplate().fillPatientDetails(patient).getRuntimeVariables());
        }   else {
            String caresDueTemplate = smsGateway.getSMSTemplate(UPCOMING_CARE_DUE_CLIENT_QUERY);
            List<String> caresDuesText = new ArrayList<String>();
            for (EnrollmentRecord enrollmentRecord : upcomingEnrollments) {
                SMSTemplate template = new SMSTemplate().fillCareSchedulesDueDate(enrollmentRecord.getScheduleName(), enrollmentRecord.getStartOfDueWindow());
                caresDuesText.add(SMS.fill(caresDueTemplate, template.getRuntimeVariables()));
            }

            String baseMsgTemplate = smsGateway.getSMSTemplate(UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY);
            Map<String, String> baseMsgTemplateValues = new SMSTemplate().fillPatientDetails(patient)
                    .fillTemplate(UPCOMING_CARE_DUE_CLIENT_QUERY, join(caresDuesText, SMS_LIST_SEPERATOR)).getRuntimeVariables();
            return SMS.fill(baseMsgTemplate, baseMsgTemplateValues);
        }
    }
}
