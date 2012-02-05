package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeliveryNotificationFormHandlerTest {

    DeliveryNotificationFormHandler deliveryNotificationFormHandler;

    @Mock
    CareService mockCareService;

    @Mock
    TextMessageService mockTextMessageService;

    @Mock
    FacilityService mockFacilityService;

    @Mock
    PatientService mockPatientService;

    @Before
    public void setUp() {
        initMocks(this);
        deliveryNotificationFormHandler = new DeliveryNotificationFormHandler();
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "careService", mockCareService);
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(deliveryNotificationFormHandler, "textMessageService", mockTextMessageService);
    }

    @Test
    public void shouldHandleDeliveryNotificationMessage() {
        final String facilityId = "12";
        final String motechId = "1234567";
        final String staffId = "123456";
        final DateTime datetime = new DateTime();
        Map<String, Object> parameter = new HashMap<String, Object>() {{
            DeliveryNotificationForm deliveryNotificationForm = new DeliveryNotificationForm() {{
                setFacilityId(facilityId);
                setMotechId(motechId);
                setStaffId(staffId);
                setDatetime(datetime);
            }};
            put("formBean", deliveryNotificationForm);
        }};
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.deliveryNotify", parameter);
        final Patient patient = new Patient(new MRSPatient(motechId, null, new MRSFacility(facilityId)));
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        String facilityPhoneNumber = "1234567890";
        when(mockFacilityService.getFacility(facilityId)).thenReturn(new Facility(new MRSFacility(facilityId)).phoneNumber(facilityPhoneNumber));

        deliveryNotificationFormHandler.handleFormEvent(event);

        final HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        verify(mockCareService).persistEncounter(motechId, staffId, facilityId, Constants.ENCOUNTER_PREGDELNOTIFYVISIT,
                datetime.toDate(), mrsObservations);
        verify(mockTextMessageService).sendSMS(facilityPhoneNumber, patient, Constants.DELIVERY_NOTIFICATION_SMS);
    }

    @Test
    public void shouldRunAsAdminUser() throws NoSuchMethodException {
        assertThat(deliveryNotificationFormHandler.getClass().getMethod("handleFormEvent",
                new Class[]{MotechEvent.class}).getAnnotation(LoginAsAdmin.class), is(not(equalTo(null))));
    }
}
