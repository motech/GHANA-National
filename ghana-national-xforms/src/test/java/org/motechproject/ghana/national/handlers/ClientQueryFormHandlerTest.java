package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.*;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

public class ClientQueryFormHandlerTest {
    private ClientQueryFormHandler clientQueryFormHandler;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private AllObservations mockAllObservations;

    @Before
    public void setUp() {
        initMocks(this);
        clientQueryFormHandler = new ClientQueryFormHandler();
        ReflectionTestUtils.setField(clientQueryFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(clientQueryFormHandler, "allObservations", mockAllObservations);
        ReflectionTestUtils.setField(clientQueryFormHandler, "smsGateway", mockSmsGateway);
    }

    @Test
    public void shouldSendMessageWithEDDForPregnantClient() {
        String firstName = "firstName";
        String lastName = "lastName";
        Date dateOfBirth = DateUtil.now().minusYears(20).toDate();
        Integer age = 30;
        String gender = "Male";
        String phoneNumber = "phoneNumber";
        String responsePhoneNumber = "responsePhoneNumber";
        Date edd = DateUtil.now().toDate();

        String facilityId = "facilityId";
        String motechId = "motechId";
        String staffId = "staffId";
        Map<String, Object> params = createMotechEventWithForm(facilityId, motechId, staffId, responsePhoneNumber);

        MRSPerson person = person(phoneNumber, dateOfBirth, age, gender, lastName, firstName);
        Patient patient = new Patient(new MRSPatient(motechId, person, new MRSFacility(facilityId)));

        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockAllObservations.findObservation(motechId, Concept.PREGNANCY.getName())).thenReturn(mockPregnancyObservationWithEDD(edd));

        clientQueryFormHandler.handleFormEvent(new MotechEvent("form.validation.successful.NurseQuery.clientQuery", params));

        ArgumentCaptor<Map> templateValuesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockSmsGateway).dispatchSMS(eq("PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY"), templateValuesCaptor.capture(), eq(responsePhoneNumber));

        Map<String, String> messageParams = templateValuesCaptor.getValue();
        assertThat(messageParams.get(PHONE_NUMBER), is(phoneNumber));
        assertThat(messageParams.get(AGE), is(age.toString()));
        assertThat(messageParams.get(GENDER), is(gender));
        assertThat(messageParams.get(FIRST_NAME), is(firstName));
        assertThat(messageParams.get(LAST_NAME), is(lastName));
        assertThat(messageParams.get(DATE), is(DateFormat.getDateInstance().format(edd)));
        assertThat(messageParams.get(DOB), is(DateFormat.getDateInstance().format(dateOfBirth)));

        
    }

    private MRSObservation mockPregnancyObservationWithEDD(Date edd) {
        MRSObservation<Concept> pregnancyObservation = new MRSObservation<Concept>(DateUtil.now().toDate(), Concept.PREGNANCY.getName(), null);
        MRSObservation eddObservation = new MRSObservation<Date>(DateUtil.now().toDate(), Concept.EDD.getName(), edd);
        pregnancyObservation.setDependantObservations(new HashSet<MRSObservation>(asList(eddObservation)));
        return pregnancyObservation;
    }

    private MRSPerson person(String phoneNumber, Date dateOfBirth, int age, String gender, String lastName, String firstName) {
        MRSPerson person = new MRSPerson();
        person.dateOfBirth(dateOfBirth);
        person.firstName(firstName);
        person.lastName(lastName);
        person.age(age);
        person.gender(gender);
        person.attributes(asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber)));
        return person;
    }

    private Map<String, Object> createMotechEventWithForm(String facilityId, String motechId, String staffId, String responsePhoneNumber) {
        final ClientQueryForm clientQueryForm = clientQueryForm(facilityId, motechId, staffId, responsePhoneNumber);
        return new HashMap<String, Object>() {{
            put(Constants.FORM_BEAN, clientQueryForm);
        }};
    }

    private ClientQueryForm clientQueryForm(String facilityId, String motechId, String staffId, String responsePhoneNumber) {
        ClientQueryForm clientQueryForm = new ClientQueryForm();
        clientQueryForm.setFacilityId(facilityId);
        clientQueryForm.setMotechId(motechId);
        clientQueryForm.setStaffId(staffId);
        clientQueryForm.setSender(responsePhoneNumber);
        return clientQueryForm;
    }
}
