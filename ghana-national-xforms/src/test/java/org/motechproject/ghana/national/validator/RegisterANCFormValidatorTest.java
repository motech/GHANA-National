package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterANCFormValidatorTest {
    private RegisterANCFormValidator registerANCFormValidator;

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    @Mock
    private PatientService mockPatientService;

    @Before
    public void setUp() {
        initMocks(this);
        registerANCFormValidator = new RegisterANCFormValidator();
        ReflectionTestUtils.setField(registerANCFormValidator, "formValidator", formValidator);
        ReflectionTestUtils.setField(registerANCFormValidator, "patientService", mockPatientService);
    }

    @Test
    public void shouldValidateRegisterANCForm() {
        RegisterANCForm formBean = mock(RegisterANCForm.class);
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);
        setupPatient(motechId, Constants.PATIENT_GENDER_FEMALE);
        registerANCFormValidator.validate(formBean);

        verify(formValidator).validatePatient(eq(motechId), eq(RegisterANCFormValidator.MOTECH_ID_ATTRIBUTE_NAME));
        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
    }

    @Test
    public void shouldThrowErrorIfThePatientIsMale() {
        String motechId = "1212";
        setupPatient(motechId, Constants.PATIENT_GENDER_MALE);

        final List<FormError> formErrors = registerANCFormValidator.validatePatient(motechId);

        assertEquals(1, formErrors.size());
        assertThat(formErrors, hasItem(new FormError(RegisterANCFormValidator.MOTECH_ID_ATTRIBUTE_NAME, RegisterANCFormValidator.GENDER_ERROR_MSG)));
    }

    @Test
    public void shouldNotThrowErrorIfThePatientIsFemale() {
        String motechId = "1212";
        setupPatient(motechId, Constants.PATIENT_GENDER_FEMALE);

        final List<FormError> formErrors = registerANCFormValidator.validatePatient(motechId);

        assertEquals(0, formErrors.size());
    }

    private void setupPatient(String motechId, String gender) {
        Patient patientMock = mock(Patient.class);
        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSPerson mrsPerson = mock(MRSPerson.class);

        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patientMock);
        when(patientMock.getMrsPatient()).thenReturn(mrsPatient);
        when(mrsPatient.getPerson()).thenReturn(mrsPerson);
        when(mrsPerson.getGender()).thenReturn(gender);
    }
}
