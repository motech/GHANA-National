package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;

@Component
public class ClientQueryFormHandler implements FormPublishHandler {

    public static final String PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY = "PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY";
    public static final String NON_PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY = "NON_PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY";

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
        Patient patient = patientService.getPatientByMotechId(clientQueryForm.getMotechId());
        Date pregnancyEDD = getActivePregnancyEDD(patient.getMotechId());
        String messageKey = (null == pregnancyEDD) ? NON_PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY : PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY;

        smsGateway.dispatchSMS(messageKey, getMessageParameters(patient, pregnancyEDD), clientQueryForm.getSender());
    }

    private Map<String, String> getMessageParameters(final Patient patient, final Date pregnancyEDD) {
        final MRSPerson person = patient.getMrsPatient().getPerson();
        return new HashMap<String, String>() {{
            put(MOTECH_ID, patient.getMotechId());
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(GENDER, person.getGender());
            put(DOB, toDateString(person.getDateOfBirth()));
            put(AGE, person.getAge().toString());
            put(FACILITY, patient.getMrsPatient().getFacility().getName());
            put(PHONE_NUMBER, person.attrValue(PatientAttributes.PHONE_NUMBER.getAttribute()));
            put(DATE, toDateString(pregnancyEDD));
        }};
    }

    private String toDateString(Date date) {
        return DateFormat.getDateInstance().format(date);
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
