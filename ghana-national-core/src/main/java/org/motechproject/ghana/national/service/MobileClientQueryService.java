package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMSTemplate;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.join;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.ghana.national.domain.Constants.SMS_LIST_SEPERATOR;

@Service
public class MobileClientQueryService {

    public static final String UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY = "UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY";
    public static final String UPCOMING_CARE_DUE_CLIENT_QUERY = "UPCOMING_CARE_DUE_CLIENT_QUERY";
    public static final String NONE_UPCOMING = "NONE_UPCOMING";

    AllSchedules allSchedules;
    private SMSGateway smsGateway;
    @Autowired
    public MobileClientQueryService(AllSchedules allSchedules, SMSGateway smsGateway) {
        this.allSchedules = allSchedules;
        this.smsGateway = smsGateway;
    }

    public void queryUpcomingCare(Patient patient, String responsePhoneNumber) {
        List<EnrollmentRecord> upcomingEnrollments = allSchedules.upcomingCareForCurrentWeek(patient.getMRSPatientId());

        if(isEmpty(upcomingEnrollments)) {
            String nocaresTemplate = smsGateway.getSMSTemplate(NONE_UPCOMING);
            smsGateway.dispatchSMS(responsePhoneNumber, SMS.fill(nocaresTemplate, new SMSTemplate().fillPatientDetails(patient).getRuntimeVariables()));
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
            smsGateway.dispatchSMS(responsePhoneNumber, SMS.fill(baseMsgTemplate, baseMsgTemplateValues));
        }
    }
}
