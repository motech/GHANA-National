package org.motechproject.ghana.national.domain.sms;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMSTemplate;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.join;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.ListUtils.union;
import static org.motechproject.ghana.national.domain.Constants.SMS_LIST_SEPERATOR;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

public class UpcomingCareSMS {

    public static final String UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY = "UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY";
    public static final String UPCOMING_CARE_DUE_CLIENT_QUERY = "UPCOMING_CARE_DUE_CLIENT_QUERY";
    public static final String NONE_UPCOMING = "NONE_UPCOMING";

    private SMSGateway smsGateway;
    private Patient patient;
    private List<EnrollmentRecord> upcomingEnrollments;
    private List<VisitResponse> upcomingAppointments;

    public UpcomingCareSMS(SMSGateway smsGateway, Patient patient, List<EnrollmentRecord> upcomingEnrollments, List<VisitResponse> upcomingAppointments) {
        this.smsGateway = smsGateway;
        this.patient = patient;
        this.upcomingEnrollments = upcomingEnrollments;
        this.upcomingAppointments = upcomingAppointments;
    }

    public String smsText() {
        if (hasUpcoming()) {
            String baseMsgTemplate = smsGateway.getSMSTemplate(UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY);
            return SMS.fill(baseMsgTemplate, new SMSTemplate().fillPatientDetails(patient)
                    .fillTemplate(UPCOMING_CARE_DUE_CLIENT_QUERY, allDueText()).getRuntimeVariables());
        } else {
            String noneCaresTemplate = smsGateway.getSMSTemplate(NONE_UPCOMING);
            return SMS.fill(noneCaresTemplate, new SMSTemplate().fillPatientDetails(patient).getRuntimeVariables());
        }
    }

    private String allDueText() {
        String dueTemplate = smsGateway.getSMSTemplate(UPCOMING_CARE_DUE_CLIENT_QUERY);
        List dueTexts = union(careDuesTexts(dueTemplate), appointmentTexts(dueTemplate));
        return join(dueTexts, SMS_LIST_SEPERATOR);
    }

    private List<String> appointmentTexts(final String dueTemplate) {
        return convert(nullSafeList(upcomingAppointments), new Converter<VisitResponse, String>() {
            public String convert(VisitResponse visitResponse) {
                return createScheduleDueText(dueTemplate, visitResponse.getName(), visitResponse.getAppointmentDueDate());
            }
        });
    }

    private List<String> careDuesTexts(final String dueTemplate) {
        return convert(nullSafeList(upcomingEnrollments), new Converter<EnrollmentRecord, String>() {
            public String convert(EnrollmentRecord enrollmentRecord) {
                return createScheduleDueText(dueTemplate, enrollmentRecord.getCurrentMilestoneName(), enrollmentRecord.getStartOfDueWindow());
            }
        });
    }

    private String createScheduleDueText(String dueTemplate, String milestoneName, DateTime dueDate) {
        SMSTemplate template = new SMSTemplate().fillCareSchedulesDueDate(milestoneName, dueDate);
        return SMS.fill(dueTemplate, template.getRuntimeVariables());
    }

    private boolean hasUpcoming() {
        return isNotEmpty(upcomingEnrollments) || isNotEmpty(upcomingAppointments);
    }
}
