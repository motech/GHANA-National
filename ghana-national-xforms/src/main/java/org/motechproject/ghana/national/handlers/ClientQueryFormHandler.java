package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.ClientQueryType;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.messagegateway.domain.MessageDispatcher;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;

@Component
public class ClientQueryFormHandler implements FormPublishHandler {

    public static final String PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY = "PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY";
    public static final String NON_PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY = "NON_PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY";
    public static final String FIND_CLIENT_RESPONSE_SMS_KEY = "FIND_CLIENT_RESPONSE_SMS_KEY";

    @Autowired
    private PatientService patientService;
    @Autowired
    private SMSGateway smsGateway;
    @Autowired
    private AllObservations allObservations;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseQuery.clientQuery")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        ClientQueryForm clientQueryForm = (ClientQueryForm) event.getParameters().get(Constants.FORM_BEAN);

        if (clientQueryForm.getQueryType().equals(ClientQueryType.CLIENT_DETAILS.toString())) {
            Patient patient = patientService.getPatientByMotechId(clientQueryForm.getMotechId());
            Date pregnancyEDD = getActivePregnancyEDD(patient.getMotechId());
            Map<String, String> messageParameters = getMessageParameters(patient, pregnancyEDD);
            messageParameters.put(PHONE_NUMBER, Utility.nullSafe(patient.getMrsPatient().getPerson().attrValue(PatientAttributes.PHONE_NUMBER.getAttribute()), ""));
            smsGateway.dispatchSMS(getTemplateKey(pregnancyEDD), messageParameters, clientQueryForm.getSender());
        } else {
            List<MRSPatient> patients = patientService.getPatients(clientQueryForm.getFirstName(), clientQueryForm.getLastName(), clientQueryForm.getPhoneNumber(), clientQueryForm.getDateOfBirth(), clientQueryForm.getNhis());
            String message = createMessage(patients);
            smsGateway.dispatchSMS(clientQueryForm.getSender(), message);
        }
    }

    private Map<String, String> getMessageParameters(Patient patient, Date pregnancyEDD) {
        Map<String, String> messageParameters;
        messageParameters = getMessageParameters(patient.getMrsPatient());
        messageParameters.put(AGE, patient.getMrsPatient().getPerson().getAge().toString());

        if (null != pregnancyEDD) {
            messageParameters.put(DATE, toDateString(pregnancyEDD));
        }
        return messageParameters;
    }

    private String getTemplateKey(Date pregnancyEDD) {
        return (null != pregnancyEDD) ? PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY : NON_PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY;
    }

    private String createMessage(List<MRSPatient> patients) {
        String messageKey = FIND_CLIENT_RESPONSE_SMS_KEY;
        StringBuilder message = new StringBuilder();
        for (MRSPatient patient : patients) {
            message.append(SMS.fill(smsGateway.getSMSTemplate(messageKey), getMessageParameters(patient)))
                   .append(MessageDispatcher.SMS_SEPARATOR);
        }
        return message.toString();
    }

    private Map<String, String> getMessageParameters(final MRSPatient mrsPatient) {
        final MRSPerson person = mrsPatient.getPerson();
        return new HashMap<String, String>() {{
            put(MOTECH_ID, mrsPatient.getMotechId());
            put(FIRST_NAME, person.getFirstName());
            put(LAST_NAME, person.getLastName());
            put(GENDER, person.getGender());
            put(DOB, toDateString(person.getDateOfBirth()));
            put(FACILITY, mrsPatient.getFacility().getName());
        }};
    }

    private String toDateString(Date date) {
        return (date != null) ? DateFormat.getDateInstance().format(date) : "";
    }

    private Date getActivePregnancyEDD(String motechId) {
        MRSObservation pregObservation = allObservations.findObservation(motechId, PREGNANCY.getName());
        if (pregObservation != null) {
            for (Object object : pregObservation.getDependantObservations()) {
                MRSObservation observation = (MRSObservation) object;
                if (observation.getConceptName().equals(EDD.getName())) {
                    return (Date) observation.getValue();
                }
            }
        }
        return null;
    }
}
