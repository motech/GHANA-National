package org.motechproject.ghana.national.validator;

import org.junit.Before;
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
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.validator.RegisterClientFormValidator.NOT_FOUND;

public class RegisterClientFormValidatorTest {
    private RegisterClientFormValidator registerClientFormValidator;

    @Mock
    private RegisterClientForm mockRegisterClientForm;
    @Mock
    private StaffService mockStaffService;
    @Mock
    private FacilityService mockFacilityService;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private MotechIdVerhoeffValidator mockMotechIdVerhoeffValidator;

    @Before
    public void setUp() {
        initMocks(this);
        registerClientFormValidator = new RegisterClientFormValidator();
        ReflectionTestUtils.setField(registerClientFormValidator, "staffService", mockStaffService);
        ReflectionTestUtils.setField(registerClientFormValidator, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(registerClientFormValidator, "patientService", mockPatientService);
    }

    @Test
    public void shouldValidateIfTheStaffWhoSubmitsTheFormIsAlreadyRegistered() {
        String userId = "0987654";
        when(mockRegisterClientForm.getStaffId()).thenReturn(userId);

        when(mockStaffService.getUserById(userId)).thenReturn(new MRSUser());

        List<FormError> formErrors = registerClientFormValidator.validate(mockRegisterClientForm);
        assertThat(formErrors, not(hasItem(new FormError("staffId", NOT_FOUND))));

        when(mockStaffService.getUserById(userId)).thenReturn(null);
        formErrors = registerClientFormValidator.validate(mockRegisterClientForm);
        assertThat(formErrors, hasItem(new FormError("staffId", NOT_FOUND)));
    }

    @Test
    public void shouldValidateIfAPatientIsAvailableWithIdAsMothersMotechId() {
        String mothersMotechId = "100";
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(mothersMotechId);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        Patient patientsMotherMock = mock(Patient.class);
        when(mockPatientService.getPatientById(mothersMotechId)).thenReturn(patientsMotherMock);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));

        when(mockPatientService.getPatientById(mothersMotechId)).thenReturn(null);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
    }

    @Test
    public void shouldReturnErrorIfPatientTypeIsChildAndMothersMotechIdIsNotPresent() {
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(null);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
        verify(mockPatientService, never()).getPatientById(Matchers.<String>any());
    }

    @Test
    public void shouldNotReturnErrorIfPatientTypeIsNotChildAndMothersMotechIdIsNotPresent() {
        when(mockRegisterClientForm.getMotherMotechId()).thenReturn(null);
        when(mockRegisterClientForm.getRegistrantType()).thenReturn(PatientType.PREGNANT_MOTHER);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));
        verify(mockPatientService, never()).getPatientById(Matchers.<String>any());
    }

    @Test
    public void shouldValidateTheMotechIdOfThePatientIfRegistrationModeIsPrePrintedId() {
        String motechId = "12345";
        when(mockRegisterClientForm.getRegistrationMode()).thenReturn(RegistrationType.USE_PREPRINTED_ID);
        Patient patientMock = mock(Patient.class);
        when(mockPatientService.getPatientById(motechId)).thenReturn(patientMock);
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("motechId", "in use")));

        when(mockPatientService.getPatientById(motechId)).thenReturn(null);
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motechId", "in use"))));
    }

    @Test
    public void shouldNotValidateMotechIdOfThePatientIfRegistrationModeIsNotPrePrintedId() {
        String motechId = "12345";
        when(mockRegisterClientForm.getRegistrationMode()).thenReturn(RegistrationType.AUTO_GENERATE_ID);
        when(mockRegisterClientForm.getMotechId()).thenReturn(motechId);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("motechId", NOT_FOUND))));
        verify(mockPatientService, never()).getPatientById(anyString());
    }

    @Test
    public void shouldValidateIfTheSelectedFacilityIsAvailable() {
        String facilityId = "0987653";
        when(mockRegisterClientForm.getFacilityId()).thenReturn(facilityId);

        Facility facilityMock = mock(Facility.class);
        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(facilityMock);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), not(hasItem(new FormError("facilityId", NOT_FOUND))));

        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(null);
        assertThat(registerClientFormValidator.validate(mockRegisterClientForm), hasItem(new FormError("facilityId", NOT_FOUND)));
    }
}
