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
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_ERR_MSG;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_PARAMETER;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class RegisterClientFormValidatorTest {
    private RegisterClientFormValidator registerClientFormValidator;

    @Mock
    private RegisterClientForm mockRegisterClientForm;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private MotechIdVerhoeffValidator mockMotechIdVerhoeffValidator;
    @Mock
    private FormValidator formValidator;


    @Before
    public void setUp() {
        initMocks(this);
        registerClientFormValidator = new RegisterClientFormValidator();
        ReflectionTestUtils.setField(registerClientFormValidator, "formValidator", formValidator);
        ReflectionTestUtils.setField(registerClientFormValidator, "patientService", mockPatientService);
    }

    @Test
    public void shouldValidateIfAPatientIsAvailableWithIdAsMothersMotechId() {
        String mothersMotechId = "100";
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(mothersMotechId);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        Patient patientsMotherMock = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(mothersMotechId)).thenReturn(patientsMotherMock);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));

        when(mockPatientService.getPatientByMotechId(mothersMotechId)).thenReturn(null);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
    }

    @Test
    public void shouldReturnErrorIfPatientTypeIsChildAndMothersMotechIdIsNotPresent() {
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(null);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
        verify(mockPatientService, never()).getPatientByMotechId(Matchers.<String>any());
    }

    @Test
    public void shouldNotReturnErrorIfPatientTypeIsNotChildAndMothersMotechIdIsNotPresent() {
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(null);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.PREGNANT_MOTHER);

        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));
        verify(mockPatientService, never()).getPatientByMotechId(Matchers.<String>any());
    }

    @Test
    public void shouldValidateTheMotechIdOfThePatientIfRegistrationModeIsPrePrintedId() {
        String motechId = "12345";
        when(mockRegisterClientForm.getRegistrationMode()).thenReturn(RegistrationType.USE_PREPRINTED_ID);
        Patient patientMock = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patientMock);
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);

        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("motechId", "in use")));

        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(null);
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motechId", "in use"))));
    }

    @Test
    public void shouldNotValidateMotechIdOfThePatientIfRegistrationModeIsNotPrePrintedId() {
        String motechId = "12345";
        when(mockRegisterClientForm.getRegistrationMode()).thenReturn(RegistrationType.AUTO_GENERATE_ID);


        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motechId", NOT_FOUND))));
        verify(mockPatientService, never()).getPatientByMotechId(anyString());
    }

    @Test
    public void shouldVerifyIfStaffIdIsValidOrNot() {
        String staffId = "21";
        when(mockRegisterClientForm.getStaffId()).thenReturn(staffId);

        registerClientFormValidator.validate(mockRegisterClientForm);
        verify(formValidator).validateIfStaffExists(eq(staffId));
    }

    @Test
    public void shouldVerifyIfFacilityIdIsValidOrNot() {
        String facilityId = "21";
        when(mockRegisterClientForm.getFacilityId()).thenReturn(facilityId);


        registerClientFormValidator.validate(mockRegisterClientForm);
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
    }

    @Test
    public void shouldReturnErrorIfChildAgeIsGreaterThanFive(){
        String motechId = "12234";
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        when(mockRegisterClientForm.getDateOfBirth()).thenReturn(new Date(99,9,9));
        when(formValidator.validateIfFacilityExists("212")).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfStaffExists("212")).thenReturn(new ArrayList<FormError>());

        List<FormError> formErrors = registerClientFormValidator.validate(mockRegisterClientForm);
        assertThat(formErrors,hasItem(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.CHILD_AGE_ERR_MSG)));

    }

    @Test
    public void shouldReturnErrorIfMotherIsNotFemale(){
        String motherMotechId = "12234";
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(motherMotechId);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.PREGNANT_MOTHER);
        when(mockRegisterClientForm.getSex()).thenReturn(Constants.PATIENT_GENDER_MALE);
        when(formValidator.validateIfFacilityExists("212")).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfStaffExists("212")).thenReturn(new ArrayList<FormError>());

        List<FormError> formErrors = registerClientFormValidator.validate(mockRegisterClientForm);
        assertThat(formErrors, hasItem(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME, Constants.GENDER_ERROR_MSG)));
    }
}
