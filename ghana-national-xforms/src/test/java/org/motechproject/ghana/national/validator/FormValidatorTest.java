package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class FormValidatorTest {
    private FormValidator formValidator;

    @Mock
    private PatientService patientService;
    @Mock
    private FacilityService facilityService;
    @Mock
    private StaffService staffService;
    private String PATIENT_ID = "patientId";

    @Before
    public void setUp() {
        initMocks(this);
        formValidator = new FormValidator();
        ReflectionTestUtils.setField(formValidator, "patientService", patientService);
        ReflectionTestUtils.setField(formValidator, "facilityService", facilityService);
        ReflectionTestUtils.setField(formValidator, "staffService", staffService);
    }

    @Test
    public void shouldValidateIfPatientIsAlive() {
        String motechId = "1234567";
        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(motechId, true);

        List<FormError> formErrors = formValidator.validateIfPatientExistsAndIsAlive(motechId, PATIENT_ID);
        assertThat(formErrors, hasItem(new FormError(PATIENT_ID, Constants.IS_NOT_ALIVE)));

        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(motechId, false);
        formErrors = formValidator.validateIfPatientExistsAndIsAlive(motechId, PATIENT_ID);
        assertThat(formErrors, not(hasItem(new FormError(PATIENT_ID, Constants.IS_NOT_ALIVE))));
    }

    private MRSPerson setupPatientServiceMockToReturnIfPatientIsAliveOrDead(String motechId, boolean isDead) {
        Patient patientMock = mock(Patient.class);
        when(patientService.getPatientByMotechId(motechId)).thenReturn(patientMock);

        MRSPatient mrsPatient = mock(MRSPatient.class);
        when(patientMock.getMrsPatient()).thenReturn(mrsPatient);

        MRSPerson mrsPerson = mock(MRSPerson.class);
        when(mrsPatient.getPerson()).thenReturn(mrsPerson);

        when(mrsPerson.isDead()).thenReturn(isDead);
        return mrsPerson;
    }

    @Test
    public void shouldValidateIfTheFacilityExists() {
        String facilityMotechId = "012345678";
        Facility facility = mock(Facility.class);
        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(null);
        List<FormError> formErrors = formValidator.validateIfFacilityExists(facilityMotechId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.FACILITY_ID, NOT_FOUND)));

        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(facility);
        formErrors = formValidator.validateIfFacilityExists(facilityMotechId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.FACILITY_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidateIfPatientExistsAndAlive() {
        String patientId = "patientId";
        when(patientService.getPatientByMotechId(patientId)).thenReturn(null);
        List<FormError> formErrors = formValidator.validateIfPatientExistsAndIsAlive(patientId, PATIENT_ID);
        assertThat(formErrors, hasItem(new FormError(PATIENT_ID, NOT_FOUND)));

        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(patientId, false);
        formErrors = formValidator.validateIfPatientExistsAndIsAlive(patientId, PATIENT_ID);
        assertThat(formErrors, not(hasItem(new FormError(PATIENT_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidatePatient() {
        String patientId = "patientId";
        when(patientService.getPatientByMotechId(patientId)).thenReturn(null);
        List<FormError> formErrors = formValidator.validatePatient(patientId, PATIENT_ID);
        assertThat(formErrors, hasItem(new FormError(PATIENT_ID, NOT_FOUND)));
    }

    @Test
    public void shouldValidateIfStaffExists() {
        String staffId = "1234567";
        when(staffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(null);

        List<FormError> formErrors = formValidator.validateIfStaffExists(staffId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.STAFF_ID, NOT_FOUND)));

        MRSUser staff = mock(MRSUser.class);
        when(staffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(staff);

        formErrors = formValidator.validateIfStaffExists(staffId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.STAFF_ID, NOT_FOUND))));
    }

    @Test
    public void shouldThrowErrorIfThePatientIsMale() {
        String motechId = "1";
        setupPatient(Constants.PATIENT_GENDER_MALE, motechId);
        final List<FormError> formErrors = formValidator.validateIfPatientIsFemale(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        assertEquals(1, formErrors.size());
        assertThat(formErrors, hasItem(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME, Constants.GENDER_ERROR_MSG)));
    }

    @Test
    public void shouldNotThrowErrorIfThePatientIsFemale() {
        String motechId = "1";
        setupPatient(Constants.PATIENT_GENDER_FEMALE, motechId);
        final List<FormError> formErrors = formValidator.validateIfPatientIsFemale(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        assertEquals(0, formErrors.size());
    }

    @Test
    public void shouldRaiseFormErrorIfChildAgeIsAboveFive() {
        String motechId = "1234567";
        Patient patientMock = mock(Patient.class);
        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSPerson mrsPerson = mock(MRSPerson.class);
        when(mrsPerson.getAge()).thenReturn(6);
        when(mrsPatient.getPerson()).thenReturn(mrsPerson);
        when(patientMock.getMrsPatient()).thenReturn(mrsPatient);
        when(patientService.getPatientByMotechId(motechId)).thenReturn(patientMock);
        List<FormError> formErrors = formValidator.validateIfPatientIsAChild(motechId);
        assertEquals(formErrors.size(), 1);
    }

    private Patient setupPatient(String gender, String motechId) {
        Patient patientMock = mock(Patient.class);
        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSPerson mrsPerson = mock(MRSPerson.class);
        when(patientService.getPatientByMotechId(motechId)).thenReturn(patientMock);
        when(patientMock.getMrsPatient()).thenReturn(mrsPatient);
        when(mrsPatient.getPerson()).thenReturn(mrsPerson);
        when(mrsPerson.getGender()).thenReturn(gender);
        return patientMock;
    }
}
