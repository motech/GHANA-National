package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.List;

import static java.util.Collections.emptyList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class RegisterANCFormValidatorTest {
    private RegisterANCFormValidator registerANCFormValidator;

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private MobileMidwifeValidator mockMobileMidwifeValidator;
    @Before
    public void setUp() {
        initMocks(this);
        registerANCFormValidator = new RegisterANCFormValidator();
        setField(registerANCFormValidator, "formValidator", formValidator);
        setField(registerANCFormValidator, "patientService", mockPatientService);
        setField(registerANCFormValidator, "mobileMidwifeValidator", mockMobileMidwifeValidator);
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

        verify(formValidator).validatePatient(eq(motechId), eq(Constants.MOTECH_ID_ATTRIBUTE_NAME));
        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
        verify(mockMobileMidwifeValidator, never()).validateForIncludeForm(Matchers.<MobileMidwifeEnrollment>any());
    }

    @Test
    public void shouldValidateMobileMidwifeIfEnrolledAlongWithANCForm() {
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        RegisterANCForm formBean = new MobileMidwifeBuilder().enroll(true).consent(false).facilityId(facilityId)
                .staffId(staffId).patientId(motechId).buildRegisterANCForm(new RegisterANCForm());

        registerANCFormValidator = spy(registerANCFormValidator);
        doReturn(emptyList()).when(registerANCFormValidator).validatePatientAndStaff(anyString(), anyString());
        
        registerANCFormValidator.validate(formBean);

        ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeValidator).validateForIncludeForm(captor.capture());
        assertThat(captor.getValue().getStaffId(), is(org.hamcrest.Matchers.equalTo(staffId)));
        assertThat(captor.getValue().getPatientId(), is(org.hamcrest.Matchers.equalTo(motechId)));
        assertThat(captor.getValue().getFacilityId(), is(org.hamcrest.Matchers.equalTo(facilityId)));
    }

    @Test
    public void shouldThrowErrorIfThePatientIsMale() {
        String motechId = "1212";
        setupPatient(motechId, Constants.PATIENT_GENDER_MALE);

        final List<FormError> formErrors = registerANCFormValidator.validateGenderOfPatient(motechId);

        assertEquals(1, formErrors.size());
        assertThat(formErrors, hasItem(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME, Constants.GENDER_ERROR_MSG)));
    }

    @Test
    public void shouldNotThrowErrorIfThePatientIsFemale() {
        String motechId = "1212";
        setupPatient(motechId, Constants.PATIENT_GENDER_FEMALE);

        final List<FormError> formErrors = registerANCFormValidator.validateGenderOfPatient(motechId);

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
