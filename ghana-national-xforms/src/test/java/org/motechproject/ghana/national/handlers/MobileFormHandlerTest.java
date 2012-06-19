package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-xforms-test.xml")
public class MobileFormHandlerTest {
    private MobileFormHandler mobileFormHandler;

    @Mock
    private PatientRegistrationFormHandler patientRegistrationFormHandler;
    @Mock
    private RegisterANCFormHandler registerANCFormHandler;
    @Mock
    private ANCVisitFormHandler ancVisitFormHandler;
    @Mock
    private CareHistoryFormHandler careHistoryFormHandler;
    @Mock
    private ClientQueryFormHandler clientQueryFormHandler;
    @Mock
    private ClientDeathFormHandler clientDeathFormHandler;
    @Mock
    private CWCVisitFormHandler cwcVisitFormHandler;
    @Mock
    private DeliveryFormHandler deliveryFormHandler;
    @Mock
    private DeliveryNotificationFormHandler deliveryNotificationFormHandler;
    @Mock
    private EditPatientFormHandler editPatientFormHandler;
    @Mock
    private MobileMidwifeFormHandler mobileMidwifeFormHandler;
    @Mock
    private OutPatientVisitFormHandler outPatientVisitFormHandler;
    @Mock
    private PNCBabyFormHandler pncBabyFormHandler;
    @Mock
    private PNCMotherFormHandler pncMotherFormHandler;
    @Mock
    private PregnancyTerminationFormHandler pregnancyTerminationFormHandler;
    @Mock
    private RegisterCWCFormHandler registerCWCFormHandler;
    @Mock
    private TTVisitFormHandler ttVisitFormHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mobileFormHandler = new MobileFormHandler();
        ReflectionTestUtils.setField(mobileFormHandler, "patientRegistrationFormHandler", patientRegistrationFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "registerANCFormHandler", registerANCFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "ancVisitFormHandler", ancVisitFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "careHistoryFormHandler", careHistoryFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "clientDeathFormHandler", clientDeathFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "clientQueryFormHandler", clientQueryFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "cwcVisitFormHandler", cwcVisitFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "deliveryFormHandler", deliveryFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "deliveryNotificationFormHandler", deliveryNotificationFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "editPatientFormHandler", editPatientFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "mobileMidwifeFormHandler", mobileMidwifeFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "outPatientVisitFormHandler", outPatientVisitFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "pncBabyFormHandler", pncBabyFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "pncMotherFormHandler", pncMotherFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "pregnancyTerminationFormHandler", pregnancyTerminationFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "registerCWCFormHandler", registerCWCFormHandler);
        ReflectionTestUtils.setField(mobileFormHandler, "ttVisitFormHandler", ttVisitFormHandler);
    }

    @Test
    public void shouldBeRegisteredAsAListenerForRegisterPatientEvent() throws NoSuchMethodException {
        String[] registeredEventSubject = mobileFormHandler.getClass().getMethod("handleFormEvent", new Class[]{MotechEvent.class}).getAnnotation(MotechListener.class).subjects();
        assertThat(registeredEventSubject, is(equalTo(new String[]{"handle.valid.xforms.group"})));
    }

    @Test
    public void shouldProcessAllTheFormsInTheGroupByInvokingTheirHandlers() {
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        final RegisterANCForm registerANCForm = new RegisterANCForm();
        final ANCVisitForm ancVisitForm = new ANCVisitForm();
        final CareHistoryForm careHistoryForm = new CareHistoryForm();
        final ClientDeathForm clientDeathForm = new ClientDeathForm();
        final ClientQueryForm clientQueryForm = new ClientQueryForm();
        final CWCVisitForm cwcVisitForm = new CWCVisitForm();
        final DeliveryNotificationForm deliveryNotificationForm = new DeliveryNotificationForm();
        final DeliveryForm deliveryForm = new DeliveryForm();
        final EditClientForm editClientForm = new EditClientForm();
        final MobileMidwifeForm mobileMidwifeForm = new MobileMidwifeForm();
        final OutPatientVisitForm outPatientVisitForm = new OutPatientVisitForm();
        final PNCBabyForm pncBabyForm = new PNCBabyForm();
        final PNCMotherForm pncMotherForm = new PNCMotherForm();
        final PregnancyTerminationForm pregnancyTerminationForm = new PregnancyTerminationForm();
        final RegisterCWCForm registerCWCForm = new RegisterCWCForm();
        final TTVisitForm ttVisitForm = new TTVisitForm();

        MotechEvent event = new MotechEvent("handle.valid.xforms.group", new HashMap<String, Object>() {{
            put("formBeanGroup", new FormBeanGroup(Arrays.<FormBean>asList(registerClientForm, registerANCForm, ancVisitForm, careHistoryForm, clientDeathForm,
                    clientQueryForm, cwcVisitForm, deliveryForm, deliveryNotificationForm, editClientForm, mobileMidwifeForm, outPatientVisitForm,
                    pncBabyForm, pncMotherForm, pregnancyTerminationForm, registerCWCForm, ttVisitForm)));
        }});

        mobileFormHandler.handleFormEvent(event);

        verify(patientRegistrationFormHandler).handleFormEvent(registerClientForm);
        verify(registerANCFormHandler).handleFormEvent(registerANCForm);
        verify(ancVisitFormHandler).handleFormEvent(ancVisitForm);
        verify(careHistoryFormHandler).handleFormEvent(careHistoryForm);
        verify(clientDeathFormHandler).handleFormEvent(clientDeathForm);
        verify(clientQueryFormHandler).handleFormEvent(clientQueryForm);
        verify(cwcVisitFormHandler).handleFormEvent(cwcVisitForm);
        verify(deliveryFormHandler).handleFormEvent(deliveryForm);
        verify(deliveryNotificationFormHandler).handleFormEvent(deliveryNotificationForm);
        verify(editPatientFormHandler).handleFormEvent(editClientForm);
        verify(mobileMidwifeFormHandler).handleFormEvent(mobileMidwifeForm);
        verify(outPatientVisitFormHandler).handleFormEvent(outPatientVisitForm);
        verify(pncBabyFormHandler).handleFormEvent(pncBabyForm);
        verify(pncMotherFormHandler).handleFormEvent(pncMotherForm);
        verify(pregnancyTerminationFormHandler).handleFormEvent(pregnancyTerminationForm);
        verify(registerCWCFormHandler).handleFormEvent(registerCWCForm);
        verify(ttVisitFormHandler).handleFormEvent(ttVisitForm);
    }
}
