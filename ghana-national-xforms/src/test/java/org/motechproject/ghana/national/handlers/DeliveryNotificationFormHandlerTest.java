package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;
import static org.motechproject.ghana.national.domain.EncounterType.PREG_DEL_NOTIFY_VISIT;
import static org.motechproject.ghana.national.handlers.DeliveryNotificationFormHandler.DELIVERY_NOTIFICATION_SMS_KEY;
import static org.motechproject.ghana.national.util.AssertionUtility.assertContainsTemplateValues;

public class DeliveryNotificationFormHandlerTest {

    DeliveryNotificationFormHandler deliveryNotificationFormHandler;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    SMSGateway mockSMSGateway;

    @Mock
    FacilityService mockFacilityService;

    @Mock
    PatientService mockPatientService;

    @Before
    public void setUp() {
        initMocks(this);
        deliveryNotificationFormHandler = new DeliveryNotificationFormHandler();
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "allEncounters", mockAllEncounters);
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "smsGateway", mockSMSGateway);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(mockPatientService).getPatientByMotechId(anyString());
        try {
            deliveryNotificationFormHandler.handleFormEvent(new MotechEvent("subject"));
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("subject"));
        }
    }

    @Test
    public void shouldHandleDeliveryNotificationMessage() {
        final String facilityId = "12";
        final String motechFacilityId = "12121";
        final String motechId = "1234567";
        final String staffId = "123456";
        final DateTime datetime = new DateTime();
        Map<String, Object> parameter = new HashMap<String, Object>() {{
            DeliveryNotificationForm deliveryNotificationForm = new DeliveryNotificationForm() {{
                setFacilityId(motechFacilityId);
                setMotechId(motechId);
                setStaffId(staffId);
                setDatetime(datetime);
            }};
            put("formBean", deliveryNotificationForm);
        }};
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.deliveryNotify", parameter);

        final String firstName = "firstName";
        final String lastName = "lastName";
        MRSPerson person = new MRSPerson().firstName(firstName).lastName(lastName).dateOfBirth(DateUtil.newDate(2000, 1, 1).toDate());
        Patient patient = new Patient(new MRSPatient(motechId, person, new MRSFacility(facilityId)));

        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        String facilityPhone = "facilityPhoneNumber";
        String additionalPhoneNumber1 = "addPhone";
        Facility facility = new Facility().phoneNumber(facilityPhone).mrsFacilityId(facilityId).additionalPhoneNumber1(additionalPhoneNumber1);
        when(mockFacilityService.getFacilityByMotechId(motechFacilityId)).thenReturn(facility);
        when(mockFacilityService.getFacility(facilityId)).thenReturn(facility);

        deliveryNotificationFormHandler.handleFormEvent(event);

        final HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        verify(mockAllEncounters).persistEncounter(new MRSPatient(motechId, person, new MRSFacility(facilityId)), staffId, facilityId, PREG_DEL_NOTIFY_VISIT.value(),
                datetime.toDate(), mrsObservations);

        ArgumentCaptor<Map> smsTemplateValuesArgCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(mockSMSGateway,times(2)).dispatchSMS(eq(DELIVERY_NOTIFICATION_SMS_KEY), smsTemplateValuesArgCaptor.capture(), captor.capture());

        List<String> allPhoneNumbers = captor.getAllValues();
        assertEquals(2, allPhoneNumbers.size());
        assertEquals(facility.getPhoneNumbers(), allPhoneNumbers);
        assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, motechId);
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastName);
        }}, smsTemplateValuesArgCaptor.getValue());
    }

    @Test
    public void shouldRunAsAdminUser() throws NoSuchMethodException {
        assertThat(deliveryNotificationFormHandler.getClass().getMethod("handleFormEvent",
                new Class[]{MotechEvent.class}).getAnnotation(LoginAsAdmin.class), is(not(equalTo(null))));
    }
}
