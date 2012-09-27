package org.motechproject.ghana.national.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.EditClientForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.validator.EditClientFormValidator;
import org.motechproject.ghana.national.validator.FormValidator;
import org.motechproject.ghana.national.validator.RegisterClientFormValidator;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.ghana.national.web.form.SearchPatientForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.ghana.national.web.helper.PatientHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.util.DateUtil;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientControllerTest {
    PatientController patientController;
    @Mock
    PatientService mockPatientService;
    @Mock
    IdentifierGenerator mockIdentifierGenerator;
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
    @Mock
    StaffService mockStaffService;
    @Mock
    MobileMidwifeService mobileMidwifeService;
    @Mock
    EditClientFormValidator mockEditClientFormValidator;
    @Mock
    private FormValidator mockFormValidator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        patientController = new PatientController();
        ReflectionTestUtils.setField(patientController, "patientService", mockPatientService);
        ReflectionTestUtils.setField(patientController, "staffService", mockStaffService);
        ReflectionTestUtils.setField(patientController, "patientHelper", mockPatientHelper);
        ReflectionTestUtils.setField(patientController, "facilityHelper", mockFacilityHelper);
        ReflectionTestUtils.setField(patientController, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(patientController, "messageSource", mockMessageSource);
        ReflectionTestUtils.setField(patientController, "motechIdVerhoeffValidator", mockMotechIdVerhoeffValidator);
        ReflectionTestUtils.setField(patientController, "identifierGenerator", mockIdentifierGenerator);
        ReflectionTestUtils.setField(patientController, "mobileMidwifeService", mobileMidwifeService);
        ReflectionTestUtils.setField(patientController, "editClientFormValidator", mockEditClientFormValidator);
        RegisterClientFormValidator registerClientFormValidator = new RegisterClientFormValidator();
        ReflectionTestUtils.setField(registerClientFormValidator, "formValidator", mockFormValidator);
        ReflectionTestUtils.setField(patientController, "registerClientFormValidator", registerClientFormValidator);
        mockBindingResult = mock(BindingResult.class);
        when(mockPatientService.registerPatient(any(Patient.class), any(String.class), Matchers.<Date>any())).thenReturn(new Patient(new MRSPatient(null, null, null)));
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
    public void shouldNotSavePatientIfStaffIdGivenIsInValid() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        PatientForm createPatientForm = new PatientForm();
        createPatientForm.setStaffId(null);
        createPatientForm.setMotechId("1267");
        createPatientForm.setFacilityId("12");
        createPatientForm.setDateOfBirth(DateUtil.today().toDate());
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(createPatientForm.getFacilityId())).thenReturn(mockFacility);
        createPatientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        String view = patientController.createPatient(createPatientForm, mockBindingResult, new ModelMap());
        verify(mockPatientService, never()).registerPatient(any(Patient.class), any(String.class), Matchers.<Date>any());
        assertEquals("patients/new", view);
    }

    @Test
    public void shouldNotCreateNewPatientIfValidationFails() throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        MRSUser mockStaff = mock(MRSUser.class);
        PatientForm createPatientForm = new PatientForm();
        createPatientForm.setStaffId("staffid");
        when(mockStaffService.getUserByEmailIdOrMotechId("staffid")).thenReturn(mockStaff);

        Facility mockFacility = mock(Facility.class);
        createPatientForm.setFacilityId("facilityid");
        when(mockFacilityService.getFacility("facilityid")).thenReturn(mockFacility);

        String patientMotechId = "1267";
        Patient mockPatient = mock(Patient.class);
        createPatientForm.setMotechId(patientMotechId);
        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(mockPatient);

        createPatientForm.setDateOfBirth(DateUtil.today().toDate());
        createPatientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        when(mockMotechIdVerhoeffValidator.isValid(patientMotechId)).thenReturn(true);

        String view = patientController.createPatient(createPatientForm, mockBindingResult, new ModelMap());

        verify(mockPatientService, never()).registerPatient(any(Patient.class), any(String.class), Matchers.<Date>any());
        assertEquals("patients/new", view);

    }

    @Test
    public void shouldNotSavePatientWhileEditingIfValidationFails() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        EditClientForm editClientForm = new EditClientForm();
        String motechId = "1267";
        String facilityId = "12";
        editClientForm.setStaffId("staffId");
        editClientForm.setMotechId(motechId);
        editClientForm.setFacilityId(facilityId);
        Facility mockFacility = mock(Facility.class);
        MRSPerson person = new MRSPerson();
        person.dead(true);
        Patient patient = new Patient(new MRSPatient(motechId, person,new MRSFacility(facilityId)));
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockEditClientFormValidator.validatePatient(motechId, Collections.<FormBean>emptyList(),Collections.<FormBean>emptyList())).thenReturn(Arrays.asList(new FormError("motechId","is not alive")));
        when(mockFacilityService.getFacility(editClientForm.getFacilityId())).thenReturn(mockFacility);
        String view = patientController.edit(new ModelMap(), motechId);
        verify(mockPatientService, never()).updatePatient(any(Patient.class), any(String.class), Matchers.<Date>any());
        assertEquals("patients/edit", view);
    }

    @Test
    public void shouldSaveUserForValidId() {
        PatientForm createPatientForm = new PatientForm();
        final String motechId = "1267";
        createPatientForm.setMotechId(motechId);
        createPatientForm.setDateOfBirth(DateUtil.today().toDate());
        String facilityId = "12";
        String facilityName = "facilityName";
        createPatientForm.setFacilityId(facilityId);
        Facility mockFacility = mock(Facility.class);
        String staffId = "1234";
        createPatientForm.setStaffId(staffId);
        MRSUser mockStaff = mock(MRSUser.class);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mockStaff);
        when(mockFacilityService.getFacility(createPatientForm.getFacilityId())).thenReturn(mockFacility);
        when(mockFacility.name()).thenReturn(facilityName);
        createPatientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        ModelMap modelMap = new ModelMap();
        when(mockMotechIdVerhoeffValidator.isValid(motechId)).thenReturn(true);
        String view = patientController.createPatient(createPatientForm, mockBindingResult, modelMap);
        assertEquals("patients/success", view);
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
        String phoneNumber = "phoneNumber";
        SearchPatientForm searchPatientForm = new SearchPatientForm(name, motechId, phoneNumber);

        String firstName = "firstName";
        Date dateOfBirth = DateUtil.newDate(2000, 11, 11).toDate();
        String sex = "M";
        String lastName = "lastName";
        String middleName = "middleName";
        Patient patient = new Patient(new MRSPatient("10", motechId, new MRSPerson().id("39").firstName(firstName).middleName(middleName).lastName(lastName).gender(sex).dateOfBirth(dateOfBirth), null));

        when(mockPatientService.search(name, motechId, phoneNumber)).thenReturn(Arrays.asList(patient));
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
        String motechId = "motechId";
        MRSPerson person = new MRSPerson();
        person.dead(false);
        Patient patient = new Patient(new MRSPatient(motechId, person,new MRSFacility("facId")));
        when(mobileMidwifeService.findActiveBy(motechId)).thenReturn(new MobileMidwifeEnrollment(DateTime.now()));
        when(mockFacilityHelper.locationMap()).thenReturn(new HashMap<String, Object>() {{
            put("key", new Object());
        }});
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        final String edit = patientController.edit(modelMap, motechId);
        assertThat(edit, is(equalTo(PatientController.EDIT_PATIENT_URL)));
    }

    @Test
    public void shouldUpdatePatientWithEditedInfo() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        String motechId = "12345";
        String staffId = "1234";
        final MRSPerson mrsPerson = new MRSPerson();
        mrsPerson.firstName("fname");
        mrsPerson.middleName("mname");
        mrsPerson.lastName("lname");
        mrsPerson.dateOfBirth(DateUtil.newDate(2000, 11, 11).toDate());
        mrsPerson.gender("male");

        MRSFacility mrsFacility = new MRSFacility("name", "country", "region", "countyDistrict", "stateProvince");
        Patient mockPatient = new Patient(new MRSPatient("2", motechId, mrsPerson, mrsFacility));
        PatientForm patientForm = new PatientForm(mockPatient);
        String facilityId = "12";
        String facilityName = "facilityName";
        patientForm.setFacilityId(facilityId);
        Facility mockFacility = mock(Facility.class);
        patientForm.setStaffId(staffId);
        MRSUser mockStaff = mock(MRSUser.class);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mockStaff);

        when(mockFacilityService.getFacility(patientForm.getFacilityId())).thenReturn(mockFacility);
        when(mockFacility.name()).thenReturn(facilityName);
        when(mockFacilityService.getFacility(facilityId)).thenReturn(mockFacility);
        when(mockPatientHelper.getPatientVO(patientForm, mockFacility)).thenReturn(mockPatient);
        String url = patientController.update(patientForm, mockBindingResult, new ModelMap());
        assertThat(PatientController.EDIT_PATIENT_URL, is(url));
        verify(mockPatientService).updatePatient(eq(mockPatient), eq(staffId), Matchers.<Date>any());

    }
}
