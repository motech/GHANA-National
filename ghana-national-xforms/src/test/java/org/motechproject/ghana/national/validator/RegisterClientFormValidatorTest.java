package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.AFTER_DOB;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class RegisterClientFormValidatorTest extends BaseUnitTest{
    private RegisterClientFormValidator registerClientFormValidator;

    @Mock
    private RegisterClientForm mockRegisterClientForm;
    @Mock
    private MotechIdVerhoeffValidator mockMotechIdVerhoeffValidator;
    @Mock
    private FormValidator formValidator;
    @Mock
    private MobileMidwifeValidator mobileMidwifeValidator;

    @Before
    public void setUp() {
        initMocks(this);
        registerClientFormValidator = new RegisterClientFormValidator();
        ReflectionTestUtils.setField(registerClientFormValidator, "formValidator", formValidator);
        ReflectionTestUtils.setField(registerClientFormValidator, "mobileMidwifeValidator", mobileMidwifeValidator);
    }

    @Test
    public void shouldValidateIfAPatientIsAvailableWithIdAsMothersMotechIdIfMotherMotechIdIsGiven() {
        super.mockCurrentDate(DateUtil.newDate(2008, 1, 1));
        String mothersMotechId = "100";
        String motechId = "001";
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setMotechId(motechId);
        registerClientForm.setMotherMotechId(mothersMotechId);
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        registerClientForm.setDateOfBirth(DateUtil.newDate(2004, 1, 1).toDate());
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setDate(DateUtil.today().toDate());

        Patient patientsMotherMock = mock(Patient.class);
        when(formValidator.getPatient(mothersMotechId)).thenReturn(patientsMotherMock);
        List<FormBean> formBeans = Arrays.<FormBean>asList(registerClientForm);
        assertThat(registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans), not(hasItem(new FormError("Mothers motech Id", NOT_FOUND))));

        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().age(4).dateOfBirth(DateUtil.newDate(2004,1,1).toDate()), new MRSFacility("facilityId")));

        registerClientForm.setMotherMotechId(null);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        assertThat(registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans), not(hasItem(new FormError("Mothers motech Id", NOT_FOUND))));

        registerClientForm.setMotherMotechId(mothersMotechId);

        when(formValidator.getPatient(mothersMotechId)).thenReturn(null);
        RegisterClientForm registerClientFormForMother = new RegisterClientForm();
        registerClientFormForMother.setMotechId(mothersMotechId);
        formBeans = Arrays.<FormBean>asList(registerClientForm, registerClientFormForMother);
        assertThat(registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans), not(hasItem(new FormError("Mothers motech Id", NOT_FOUND))));
    }

    @Test
    public void shouldNotReturnErrorIfPatientTypeIsNotChildAndMothersMotechIdIsNotPresent() {
        Patient patient = new Patient(new MRSPatient("motechId",new MRSPerson().gender("F").dateOfBirth(DateUtil.newDate(2004,1,1).toDate()),new MRSFacility("facilityId")));
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setMotherMotechId(null);
        registerClientForm.setRegistrantType(PatientType.PREGNANT_MOTHER);
        registerClientForm.setMotechId("motechId");
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setDateOfBirth(DateUtil.newDate(1999, 9, 9).toDate());
        registerClientForm.setDate(DateUtil.today().toDate());

        when(formValidator.getPatient(registerClientForm.getMotechId())).thenReturn(patient);

        List<FormBean> formBeans = Arrays.<FormBean>asList(registerClientForm);
        assertThat(registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans), not(hasItem(new FormError("Mothers motech Id", NOT_FOUND))));
        verify(formValidator, times(1)).getPatient(Matchers.<String>any());
    }

    @Test
    public void shouldValidateTheMotechIdOfThePatientIfRegistrationModeIsPrePrintedId() {
        String motechId = "1234567";
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setMotechId(motechId);
        registerClientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setDateOfBirth(DateUtil.newDate(1999, 9, 9).toDate());
        registerClientForm.setRegistrantType(PatientType.OTHER);
        registerClientForm.setAddHistory(false);
        registerClientForm.setDate(DateUtil.today().toDate());

        Patient patientMock = mock(Patient.class);
        when(formValidator.getPatient(motechId)).thenReturn(patientMock);
        when(patientMock.dateOfBirth()).thenReturn(DateUtil.newDate(2000,12,12).toDateTimeAtCurrentTime());
        List<FormBean> formBeans = Arrays.<FormBean>asList(registerClientForm);
        List<FormError> formErrors = registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(formErrors, hasItem(new FormError("motechId", "in use")));
        when(formValidator.getPatient(motechId)).thenReturn(null);
        formErrors = registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(formErrors, not(hasItem(new FormError("motechId", "in use"))));
    }

    @Test
    public void shouldNotValidateMotechIdOfThePatientIfRegistrationModeIsNotPrePrintedId() {
        String motechId = "12345";
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setRegistrationMode(RegistrationType.AUTO_GENERATE_ID);
        registerClientForm.setRegistrantType(PatientType.PREGNANT_MOTHER);
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setDateOfBirth(DateUtil.newDate(1999, 9, 9).toDate());
        registerClientForm.setDate(DateUtil.today().toDate());

        List<FormBean> formBeans = Arrays.<FormBean>asList(registerClientForm);
        assertThat(registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans), not(hasItem(new FormError("motechId", "in use"))));
        verify(formValidator, never()).getPatient(anyString());
    }

    @Test
    public void shouldVerifyIfStaffIdIsValidOrNot() {
        String staffId = "21";
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setStaffId(staffId);
        registerClientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setDateOfBirth(DateUtil.newDate(1999, 9, 9).toDate());
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        registerClientForm.setDate(DateUtil.today().toDate());

        List<FormBean> formBeans = Arrays.<FormBean>asList(registerClientForm);
        registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans);
        verify(formValidator).validateIfStaffExists(eq(staffId));
    }
    
    @Test
    public void shouldVerifyIfFacilityIdIsValidOrNot() {
        String facilityId = "21";
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setFacilityId(facilityId);
        registerClientForm.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setDateOfBirth(DateUtil.newDate(1999, 9, 9).toDate());
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        registerClientForm.setDate(DateUtil.today().toDate());

        List<FormBean> formBeans = Arrays.<FormBean>asList(registerClientForm);
        registerClientFormValidator.validate(registerClientForm, new FormBeanGroup(formBeans), formBeans);
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
    }

    @Test
    public void shouldReturnErrorIfChildAgeIsGreaterThanFive() {
        String motechId = "12234";
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        when(mockRegisterClientForm.getDateOfBirth()).thenReturn(new Date(99, 9, 9));
        when(mockRegisterClientForm.getFormname()).thenReturn("registerPatient");
        when(formValidator.validateIfFacilityExists("212")).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfStaffExists("212")).thenReturn(new ArrayList<FormError>());

        List<FormBean> formBeans = Arrays.<FormBean>asList(mockRegisterClientForm);
        List<FormError> formErrors = registerClientFormValidator.validate(mockRegisterClientForm, new FormBeanGroup(formBeans), formBeans);

        assertThat(formErrors, hasItem(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.CHILD_AGE_MORE_ERR_MSG)));
    }

    @Test
    public void shouldReturnErrorIfMotherIsNotFemale() {
        String motherMotechId = "12234";
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(motherMotechId);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.PREGNANT_MOTHER);
        when(mockRegisterClientForm.getSex()).thenReturn(Constants.PATIENT_GENDER_MALE);
        when(formValidator.validateIfFacilityExists("212")).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfStaffExists("212")).thenReturn(new ArrayList<FormError>());
        when(mockRegisterClientForm.getFormname()).thenReturn("registerPatient");
        when(mockRegisterClientForm.getDateOfBirth()).thenReturn(DateUtil.newDate(2000,12,12).toDate());
        List<FormBean> formBeans = Arrays.<FormBean>asList(mockRegisterClientForm);
        List<FormError> formErrors = registerClientFormValidator.validate(mockRegisterClientForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(formErrors, hasItem(new FormError("Sex", Constants.GENDER_ERROR_MSG)));
    }

    @Test
    public void shouldReturnErrorForInvalidHistoryDates() {
        String motechId="motechId";
        RegisterClientForm formBean = mock(RegisterClientForm.class);
        
        when(formValidator.getPatient(formBean.getMotechId())).thenReturn(null);
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getAddHistory()).thenReturn(true);
        when(formBean.getHistoryDatesMap()).thenReturn(new HashMap<String,Date>(){{
            put("date1", DateUtil.newDate(2000,12,12).toDate());
            put("date2", DateUtil.newDate(2011,12,12).toDate());
            put("date3", DateUtil.newDate(2012,12,12).toDate());
        }});
        when(formBean.getFormname()).thenReturn("registerPatient");
        when(formBean.getDateOfBirth()).thenReturn(DateUtil.newDate(2012,1,1).toDate());

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean);
        List<FormError> errors = registerClientFormValidator.validate(formBean,new FormBeanGroup(formBeans),formBeans);
        
        assertThat(errors,hasItem(new FormError("date1",AFTER_DOB)));
        assertThat(errors,hasItem(new FormError("date2",AFTER_DOB)));
        assertThat(errors,not(hasItem(new FormError("date3",AFTER_DOB))));
    }
}
