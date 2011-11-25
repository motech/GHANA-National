package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.UserService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.User;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.validator.RegisterClientFormValidator.NOT_FOUND;

public class RegisterClientFormValidatorTest {
    private RegisterClientFormValidator registerClientFormValidator;

    @Mock
    private RegisterClientForm registerClientForm;
    @Mock
    private UserService userService;
    @Mock
    private FacilityService facilityService;
    @Mock
    private PatientService patientService;
    @Mock
    private MotechIdVerhoeffValidator motechIdVerhoeffValidator;

    @Before
    public void setUp() {
        initMocks(this);
        registerClientFormValidator = new RegisterClientFormValidator();
        ReflectionTestUtils.setField(registerClientFormValidator, "userService", userService);
        ReflectionTestUtils.setField(registerClientFormValidator, "facilityService", facilityService);
        ReflectionTestUtils.setField(registerClientFormValidator, "patientService", patientService);
    }

    @Test
    public void shouldValidateIfTheStaffWhoSubmitsTheFormIsAlreadyRegistered() {
        String userId = "0987654";
        when(registerClientForm.getStaffId()).thenReturn(userId);

        when(userService.getUserById(userId)).thenReturn(new User());

        List<FormError> formErrors = registerClientFormValidator.validate(registerClientForm);
        assertThat(formErrors, not(hasItem(new FormError("staffId", NOT_FOUND))));

        when(userService.getUserById(userId)).thenReturn(null);
        formErrors = registerClientFormValidator.validate(registerClientForm);
        assertThat(formErrors, hasItem(new FormError("staffId", NOT_FOUND)));
    }

    @Ignore
    @Test
    public void shouldValidateIfThePrePrintedMotechIdIsAValidId() {
    }

    @Test
    public void shouldValidateIfAPatientIsAvailableWithIdAsMothersMotechId() {
        String mothersMotechId = "100";
        when(registerClientForm.getMotherMotechId()).thenReturn(mothersMotechId);
        when(registerClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        Patient patientsMotherMock = mock(Patient.class);
        when(patientService.getPatientById(mothersMotechId)).thenReturn(patientsMotherMock);
        assertThat(registerClientFormValidator.validate(registerClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));

        when(patientService.getPatientById(mothersMotechId)).thenReturn(null);
        assertThat(registerClientFormValidator.validate(registerClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
    }

    @Test
    public void shouldReturnErrorIfPatientTypeIsChildAndMothersMotechIdIsNotPresent() {
        when(registerClientForm.getMotherMotechId()).thenReturn(null);
        when(registerClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        assertThat(registerClientFormValidator.validate(registerClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
        verify(patientService, never()).getPatientById(Matchers.<String>any());
    }

    @Test
    public void shouldNotReturnErrorIfPatientTypeIsNotChildAndMothersMotechIdIsNotPresent() {
        when(registerClientForm.getMotherMotechId()).thenReturn(null);
        when(registerClientForm.getRegistrantType()).thenReturn(PatientType.PREGNANT_MOTHER);
        assertThat(registerClientFormValidator.validate(registerClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));
        verify(patientService, never()).getPatientById(Matchers.<String>any());
    }

    @Test
    public void shouldValidateTheMotechIdOfThePatientIfRegistrationModeIsPrePrintedId() {
        String motechId = "12345";
        when(registerClientForm.getRegistrationMode()).thenReturn(RegistrationType.USE_PREPRINTED_ID);
        Patient patientMock = mock(Patient.class);
        when(patientService.getPatientById(motechId)).thenReturn(patientMock);
        when(registerClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(registerClientForm), hasItem(new FormError("motechId", "in use")));

        when(patientService.getPatientById(motechId)).thenReturn(null);
        when(registerClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(registerClientForm), not(hasItem(new FormError("motechId", "in use"))));
    }

    @Test
    public void shouldNotValidateMotechIdOfThePatientIfRegistrationModeIsNotPrePrintedId() {
        String motechId = "12345";
        when(registerClientForm.getRegistrationMode()).thenReturn(RegistrationType.AUTO_GENERATE_ID);
        when(registerClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(registerClientForm), not(hasItem(new FormError("motechId", NOT_FOUND))));
        verify(patientService, never()).getPatientById(anyString());
    }

    @Test
    public void shouldValidateIfTheSelectedFacilityIsAvailable() {
        String facilityId = "0987653";
        when(registerClientForm.getFacilityId()).thenReturn(facilityId);

        Facility facilityMock = mock(Facility.class);
        when(facilityService.getFacility(facilityId)).thenReturn(facilityMock);
        assertThat(registerClientFormValidator.validate(registerClientForm), not(hasItem(new FormError("facilityId", NOT_FOUND))));

        when(facilityService.getFacility(facilityId)).thenReturn(null);
        assertThat(registerClientFormValidator.validate(registerClientForm), hasItem(new FormError("facilityId", NOT_FOUND)));
    }
}
