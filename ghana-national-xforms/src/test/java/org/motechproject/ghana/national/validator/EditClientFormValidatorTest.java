package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.EditClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class EditClientFormValidatorTest {
    private EditClientFormValidator editClientFormValidator;

    @Mock
    private EditClientForm mockEditClientForm;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private MotechIdVerhoeffValidator mockMotechIdVerhoeffValidator;
    @Mock
    private FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        editClientFormValidator = new EditClientFormValidator();
        ReflectionTestUtils.setField(editClientFormValidator, "formValidator", formValidator);
        ReflectionTestUtils.setField(editClientFormValidator, "patientService", mockPatientService);
    }

    @Test
    public void shouldValidateIfAPatientIsAvailableWithIdAsMothersMotechId() {
        String mothersMotechId = "100";
        when(mockEditClientForm.getMotherMotechId()).thenReturn(mothersMotechId);
        Patient patientsMotherMock = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(mothersMotechId)).thenReturn(patientsMotherMock);
        assertThat(editClientFormValidator.validate(mockEditClientForm), not(hasItem(new FormError("motherMotechId", NOT_FOUND))));

        when(mockPatientService.getPatientByMotechId(mothersMotechId)).thenReturn(null);
        assertThat(editClientFormValidator.validate(mockEditClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
    }

    @Test
    public void shouldReturnErrorIfMothersMotechIdIsNotPresent() {
        String motherId = "12345";
        when(mockEditClientForm.getMotherMotechId()).thenReturn(motherId);
        when(mockPatientService.getPatientByMotechId(motherId)).thenReturn(null);
        assertThat(editClientFormValidator.validate(mockEditClientForm), hasItem(new FormError("motherMotechId", NOT_FOUND)));
    }

    @Test
    public void shouldValidateTheMotechIdOfThePatient() {
        String motechId = "12345";
        Patient mockPatient = mock(Patient.class);
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(null);
        when(mockEditClientForm.getMotechId()).thenReturn(motechId);
        assertThat(editClientFormValidator.validate(mockEditClientForm), hasItem(new FormError("motechId", NOT_FOUND)));

        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(mockPatient);
        when(mockEditClientForm.getMotechId()).thenReturn(motechId);
        assertThat(editClientFormValidator.validate(mockEditClientForm), not(hasItem(new FormError("motechId", NOT_FOUND))));
    }

    @Test
    public void shouldVerifyIfStaffIdIsValidOrNot() {
        String staffId = "21";
        when(mockEditClientForm.getStaffId()).thenReturn(staffId);
        editClientFormValidator.validate(mockEditClientForm);
        verify(formValidator).validateIfStaffExists(eq(staffId));
    }

    @Test
    public void shouldVerifyIfFacilityIdIsValidOrNot() {
        String facilityId = "21";
        when(mockEditClientForm.getFacilityId()).thenReturn(facilityId);
        editClientFormValidator.validate(mockEditClientForm);
        verify(formValidator).validateIfFacilityExists(eq(facilityId));

    }

    @Test
    public void shouldVerifyIfFacilityIdsAreValidOrNot() {
        String facilityId = "21";
        String encounterFacilityId = "31";
        when(mockEditClientForm.getFacilityId()).thenReturn(facilityId);
        when(mockEditClientForm.getUpdatePatientFacilityId()).thenReturn(encounterFacilityId);
        editClientFormValidator.validate(mockEditClientForm);
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
        verify(formValidator).validateIfFacilityExists(eq(encounterFacilityId));
    }
}
