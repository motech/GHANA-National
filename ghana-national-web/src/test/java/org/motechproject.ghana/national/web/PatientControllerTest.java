package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.ghana.national.web.form.SearchPatientForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.ghana.national.web.helper.PatientHelper;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientControllerTest {
    PatientController patientController;
    @Mock
    PatientService mockPatientService;
    @Mock
    IdentifierGenerationService mockIdentifierGenerationService;
    @Mock
    MessageSource mockMessageSource;
    @Mock
    FacilityHelper mockFacilityHelper;
    @Mock
    PatientHelper mockPatientHelper;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    BindingResult mockBindingResult;
    @Mock
    MotechIdVerhoeffValidator mockMotechIdVerhoeffValidator;

    @Before
    public void setUp() {
        initMocks(this);
        patientController = new PatientController();
        ReflectionTestUtils.setField(patientController, "patientService", mockPatientService);
        ReflectionTestUtils.setField(patientController, "patientHelper", mockPatientHelper);
        ReflectionTestUtils.setField(patientController, "facilityHelper", mockFacilityHelper);
        ReflectionTestUtils.setField(patientController, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(patientController, "messageSource", mockMessageSource);
        ReflectionTestUtils.setField(patientController, "motechIdVerhoeffValidator", mockMotechIdVerhoeffValidator);
        ReflectionTestUtils.setField(patientController, "identifierGenerationService", mockIdentifierGenerationService);
        mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void shouldRenderPageToCreateAPatient() {
        final ModelMap modelMap = new ModelMap();
        final String key = "key";
        when(mockFacilityHelper.locationMap()).thenReturn(new HashMap<String, Object>() {{
            put(key, new Object());
        }});
        patientController.newPatientForm(modelMap);
        verify(mockFacilityHelper).locationMap();
        assertNotNull(modelMap.get(key));
    }

    @Test
    public void shouldFormatDateInDD_MM_YYYYFormat() throws ParseException {
        WebDataBinder binder = mock(WebDataBinder.class);
        patientController.initBinder(binder);
        final ArgumentCaptor<CustomDateEditor> captor = ArgumentCaptor.forClass(CustomDateEditor.class);
        verify(binder).registerCustomEditor(eq(Date.class), captor.capture());

        CustomDateEditor registeredEditor = captor.getValue();
        final Field dateFormatField = ReflectionUtils.findField(CustomDateEditor.class, "dateFormat");
        dateFormatField.setAccessible(true);
        SimpleDateFormat dateFormat = (SimpleDateFormat) ReflectionUtils.getField(dateFormatField, registeredEditor);
        assertThat(dateFormat.parse("23/11/2001"), is(new SimpleDateFormat("dd/MM/yyyy").parse("23/11/2001")));
    }

    @Test
    public void shouldSaveUserForValidId() {
        PatientForm createPatientForm = new PatientForm();
        final String motechId = "1267";
        createPatientForm.setMotechId(motechId);
        String facilityId = "12";
        String facilityName = "facilityName";
        createPatientForm.setFacilityId(facilityId);
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(createPatientForm.getFacilityId())).thenReturn(mockFacility);
        when(mockFacility.name()).thenReturn(facilityName);
        createPatientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        ModelMap modelMap = new ModelMap();
        when(mockMotechIdVerhoeffValidator.isValid(motechId)).thenReturn(true);
        String view = patientController.createPatient(createPatientForm, mockBindingResult, modelMap);
        assertEquals(view, "patients/success");
    }

    @Test
    public void shouldRenderSearchPatientPage() {
        ModelMap modelMap = new ModelMap();
        assertThat(patientController.search(modelMap), is(equalTo("patients/search")));
        assertThat(((SearchPatientForm) modelMap.get(PatientController.SEARCH_PATIENT_FORM)).getMotechId(), is(equalTo(null)));
        assertThat(((SearchPatientForm) modelMap.get(PatientController.SEARCH_PATIENT_FORM)).getName(), is(equalTo(null)));
        assertThat(((SearchPatientForm) modelMap.get(PatientController.SEARCH_PATIENT_FORM)).getPatientForms(), is(equalTo(null)));
    }

    @Test
    public void shouldSearchForPatientByNameOrId() {
        String motechId = "12345";
        String name = "name";
        SearchPatientForm searchPatientForm = new SearchPatientForm(name, motechId);

        String firstName = "firstName";
        Date dateOfBirth = new Date(2000, 11, 11);
        String sex = "M";
        String lastName = "lastName";
        String middleName = "middleName";
        Patient patient = new Patient(new MRSPatient("10", motechId, new MRSPerson().id("39").firstName(firstName).middleName(middleName).lastName(lastName).gender(sex).dateOfBirth(dateOfBirth), null));

        when(mockPatientService.search(name, motechId)).thenReturn(Arrays.asList(patient));
        ModelMap modelMapPassedToTheView = new ModelMap();

        String returnedUrl = patientController.search(searchPatientForm, modelMapPassedToTheView);

        SearchPatientForm patientsReturnedBySearch = (SearchPatientForm) modelMapPassedToTheView.get(PatientController.SEARCH_PATIENT_FORM);

        assertThat(patientsReturnedBySearch.getPatientForms().size(), is(equalTo(1)));
        assertThat(patientsReturnedBySearch.getPatientForms().get(0).getFirstName(), is(equalTo(firstName)));
        assertThat(patientsReturnedBySearch.getPatientForms().get(0).getMiddleName(), is(equalTo(middleName)));
        assertThat(patientsReturnedBySearch.getPatientForms().get(0).getLastName(), is(equalTo(lastName)));
        assertThat(patientsReturnedBySearch.getPatientForms().get(0).getSex(), is(equalTo(sex)));
        assertThat(patientsReturnedBySearch.getPatientForms().get(0).getDateOfBirth(), is(equalTo(dateOfBirth)));

        assertThat(returnedUrl, is(equalTo(PatientController.SEARCH_PATIENT_URL)));
    }

    @Test
    public void shouldReturnEditPatientForm() {
        final ModelMap modelMap = new ModelMap();

        when(mockFacilityHelper.locationMap()).thenReturn(new HashMap<String, Object>() {{
            put("key", new Object());
        }});
        String motechId = "";
        final String edit = patientController.edit(modelMap, motechId);
        assertThat(edit, is(equalTo(PatientController.EDIT_PATIENT_URL)));
    }

    @Test
    public void shouldUpdatePatientWithEditedInfo() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        String motechId = "12345";
        final MRSPerson mrsPerson = new MRSPerson();
        mrsPerson.firstName("fname");
        mrsPerson.middleName("mname");
        mrsPerson.lastName("lname");
        mrsPerson.dateOfBirth(new Date(2000, 11, 11));
        mrsPerson.gender("male");

        MRSFacility mrsFacility = new MRSFacility("name", "country", "region", "countyDistrict", "stateProvince");
        Patient mockPatient = new Patient(new MRSPatient("2", motechId, mrsPerson, mrsFacility));
        PatientForm patientForm = new PatientForm(mockPatient);
        String facilityId = "12";
        String facilityName = "facilityName";
        patientForm.setFacilityId(facilityId);
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(patientForm.getFacilityId())).thenReturn(mockFacility);
        when(mockFacility.name()).thenReturn(facilityName);
        when(mockFacilityService.getFacility(facilityId)).thenReturn(mockFacility);
        when(mockPatientHelper.getPatientVO(patientForm, mockFacility)).thenReturn(mockPatient);
        String url = patientController.update(patientForm, mockBindingResult, new ModelMap());
        assertThat(PatientController.EDIT_PATIENT_URL, is(url));
        verify(mockPatientService).updatePatient(eq(mockPatient), eq(patientForm.getTypeOfPatient()), eq(patientForm.getParentId()));

    }
}
