package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.ClientQueryType;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.PregnancyService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.Constants.NO_MATCHING_RECORDS_FOUND;
import static org.motechproject.ghana.national.domain.Constants.PATTERN_DD_MMM_YYYY;
import static org.motechproject.ghana.national.handlers.ClientQueryFormHandler.FIND_CLIENT_RESPONSE_SMS_KEY;
import static org.motechproject.ghana.national.util.AssertionUtility.assertContainsTemplateValues;
import static org.motechproject.util.DateUtil.today;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ClientQueryFormHandlerTest {
    private ClientQueryFormHandler clientQueryFormHandler;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private PregnancyService mockPregnancyService;
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private AllCareSchedules mockAllCareSchedules;
    @Mock
    private AllAppointments mockAllAppointments;

    @Before
    public void setUp() {
        initMocks(this);
        clientQueryFormHandler = new ClientQueryFormHandler();
        setField(clientQueryFormHandler, "patientService", mockPatientService);
        setField(clientQueryFormHandler, "pregnancyService", mockPregnancyService);
        setField(clientQueryFormHandler, "smsGateway", mockSmsGateway);
        setField(clientQueryFormHandler, "allCareSchedules", mockAllCareSchedules);
        setField(clientQueryFormHandler, "allAppointments", mockAllAppointments);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(mockPatientService).getPatientByMotechId(anyString());
        try {
            ClientQueryForm clientQueryForm = new ClientQueryForm();
            clientQueryForm.setQueryType(ClientQueryType.CLIENT_DETAILS.toString());
            clientQueryFormHandler.handleFormEvent(clientQueryForm);
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered error while processing client query form"));
        }
    }

    @Test
    public void shouldSendMessageWithEDDForPregnantClientForClientDetailsQuery() {
        final String firstName = "firstName";
        final String lastName = "lastName";
        final Date dateOfBirth = DateUtil.now().minusYears(20).toDate();
        final Integer age = 30;
        final String gender = "Male";
        final String phoneNumber = "phoneNumber";
        final String responsePhoneNumber = "responsePhoneNumber";
        final Date edd = DateUtil.now().toDate();

        String facilityId = "facilityId";
        String motechId = "motechId";
        String staffId = "staffId";
        ClientQueryForm clientQueryForm = createMotechEventWithForm(facilityId, motechId, staffId, responsePhoneNumber, ClientQueryType.CLIENT_DETAILS.toString());

        MRSPerson person = person(phoneNumber, dateOfBirth, age, gender, lastName, firstName);
        Patient patient = new Patient(new MRSPatient(motechId, person, new MRSFacility(facilityId)));

        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockPregnancyService.activePregnancyEDD(motechId)).thenReturn(edd);

        clientQueryFormHandler.handleFormEvent(clientQueryForm);

        ArgumentCaptor<Map> templateValuesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockSmsGateway).dispatchSMS(eq("PREGNANT_CLIENT_QUERY_RESPONSE_SMS_KEY"), templateValuesCaptor.capture(), eq(responsePhoneNumber));

        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(PHONE_NUMBER, phoneNumber);
            put(AGE, age.toString());
            put(GENDER, gender);
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastName);
            put(DATE, new SimpleDateFormat(PATTERN_DD_MMM_YYYY).format(edd));
            put(DOB, new SimpleDateFormat(PATTERN_DD_MMM_YYYY).format(dateOfBirth));

        }}, templateValuesCaptor.getValue());
    }

    @Test
    public void shouldSendMessageForFindClientIDQuery() {
        final String firstName = "firstName";
        final String lastName = "lastName";
        final Date dateOfBirth = DateUtil.now().minusYears(20).toDate();
        String dateString = new SimpleDateFormat(PATTERN_DD_MMM_YYYY).format(dateOfBirth);
        String responsePhoneNumber = "responsePhoneNumber";
        final String facilityId = "facilityId";
        final String motechId = "motechId";
        final MRSFacility mrsFacility = new MRSFacility(facilityId, "name", null, null, null, null);

        ClientQueryForm clientQueryFormForFindClientId = createClientQueryFormForFindClientId(firstName, lastName, dateOfBirth, null, responsePhoneNumber, facilityId, motechId, ClientQueryType.FIND_CLIENT_ID);

        ArrayList<MRSPatient> patients = new ArrayList<MRSPatient>() {{
            add(new MRSPatient(motechId, new MRSPerson().lastName(lastName).firstName(firstName).dateOfBirth(dateOfBirth).gender("F"), mrsFacility));
            add(new MRSPatient("45423", new MRSPerson().lastName(lastName).firstName("first").gender("M").dateOfBirth(new Date(189, 5, 6)), mrsFacility));
        }};
        when(mockPatientService.getPatients(firstName, lastName, null, dateOfBirth, null)).thenReturn(patients);
        when(mockSmsGateway.getSMSTemplate(FIND_CLIENT_RESPONSE_SMS_KEY)).thenReturn("MoTeCH ID=${motechId},${firstName},${lastName}, Sex=${gender}, DoB=${dob}, ${facility}");

        clientQueryFormHandler.handleFormEvent(clientQueryFormForFindClientId);

        ArgumentCaptor<String> templateValuesCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSmsGateway).dispatchSMS(eq(responsePhoneNumber), templateValuesCaptor.capture());

        String expectedMessage = "MoTeCH ID=motechId,firstName,lastName, Sex=F, DoB=" + dateString + ", name%0aMoTeCH ID=45423,first,lastName, Sex=M, DoB=06 Jun 2089, name%0a";
        assertEquals(expectedMessage, templateValuesCaptor.getValue());
    }

    @Test
    public void shouldSendNoMatchingRecordsFoundIfNoRecordsFound() {
        final String firstName = "firstName";
        final String lastName = "lastName";
        final Date dateOfBirth = DateUtil.now().minusYears(20).toDate();
        String responsePhoneNumber = "responsePhoneNumber";
        final String facilityId = "facilityId";

        ClientQueryForm clientQueryFormForFindClientId = createClientQueryFormForFindClientId(firstName, lastName, dateOfBirth, "", responsePhoneNumber, facilityId, null, ClientQueryType.FIND_CLIENT_ID);

        when(mockPatientService.getPatients(firstName, lastName, null, dateOfBirth, null)).thenReturn(new ArrayList<MRSPatient>());
        when(mockSmsGateway.getSMSTemplate(FIND_CLIENT_RESPONSE_SMS_KEY)).thenReturn("MoTeCH ID=${motechId}, FirstName=${firstName}, LastName=${lastName}, Sex=${gender}, DoB=${dob}, Facility=${facility}");

        clientQueryFormHandler.handleFormEvent(clientQueryFormForFindClientId);

        ArgumentCaptor<String> templateValuesCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSmsGateway).dispatchSMS(eq(responsePhoneNumber), templateValuesCaptor.capture());

        assertEquals(NO_MATCHING_RECORDS_FOUND, templateValuesCaptor.getValue());
    }

    @Test
    public void shouldSendUpcomingCareDetailsIfQueryTypeIsUpcomingCare() {
        final String motechId = "motech-id";
        final String facilityId = "facilityId";
        final String staffId = "staffId";

        String responsePhoneNumber = "94423232";
        String mrsPatientId = "patientId";

        ClientQueryForm clientQueryForm = createMotechEventWithForm(facilityId, motechId, staffId, responsePhoneNumber, ClientQueryType.UPCOMING_CARE.toString());

        final Patient patient = new Patient(new MRSPatient(mrsPatientId, motechId, new MRSPerson().dateOfBirth(today().toDate()), null));
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockSmsGateway.getSMSTemplate(anyString())).thenReturn("some template");

        clientQueryFormHandler.handleFormEvent(clientQueryForm);

        verify(mockAllCareSchedules).upcomingCareForCurrentWeek(mrsPatientId);
        verify(mockAllAppointments).upcomingAppointmentsForCurrentWeek(motechId);
        verify(mockSmsGateway).dispatchSMS(eq(responsePhoneNumber), anyString());
    }

    private ClientQueryForm createClientQueryFormForFindClientId(String firstName, String lastName, Date dateOfBirth, String phoneNumber, String senderResponseNumber, String facilityId, String motechId, ClientQueryType clientQueryType) {
        final ClientQueryForm clientQueryForm = clientQueryForm(facilityId, motechId, "323", senderResponseNumber, clientQueryType.toString());
        clientQueryForm.setFirstName(firstName);
        clientQueryForm.setLastName(lastName);
        clientQueryForm.setDateOfBirth(dateOfBirth);
        clientQueryForm.setPhoneNumber(phoneNumber);
        clientQueryForm.setNhis(null);

        return clientQueryForm;
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

    private ClientQueryForm createMotechEventWithForm(String facilityId, String motechId, String staffId, String responsePhoneNumber, String clientQueryType) {
        return clientQueryForm(facilityId, motechId, staffId, responsePhoneNumber, clientQueryType);
    }

    private ClientQueryForm clientQueryForm(String facilityId, String motechId, String staffId, String senderPhoneNumber, String clientQueryType) {
        ClientQueryForm clientQueryForm = new ClientQueryForm();
        clientQueryForm.setFacilityId(facilityId);
        clientQueryForm.setMotechId(motechId);
        clientQueryForm.setStaffId(staffId);
        clientQueryForm.setSender(senderPhoneNumber);
        clientQueryForm.setQueryType(clientQueryType);
        return clientQueryForm;
    }
}
