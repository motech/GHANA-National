package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

import java.util.ArrayList;

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

    @Before
    public void setUp(){
        initMocks(this);
        formValidator = new FormValidator();
        ReflectionTestUtils.setField(formValidator, "patientService", patientService);
        ReflectionTestUtils.setField(formValidator, "facilityService", facilityService);
        ReflectionTestUtils.setField(formValidator, "staffService", staffService);
    }

    @Test
    public void shouldValidateIfPatientIsAlive(){
        String motechId = "1234567";
        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(motechId, true);

        ArrayList<FormError> formErrors = new ArrayList<FormError>();
        formValidator.validatePatient(formErrors, motechId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.PATIENT_ID, FormValidator.IS_NOT_ALIVE)));

        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(motechId, false);
        formErrors = new ArrayList<FormError>();
        formValidator.validatePatient(formErrors, motechId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.PATIENT_ID, FormValidator.IS_NOT_ALIVE))));
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
    public void shouldValidateIfTheFacilityExists(){
        String facilityMotechId = "012345678";
        Facility facility = mock(Facility.class);
        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(null);
        ArrayList<FormError> formErrors = new ArrayList<FormError>();
        formValidator.validateIfFacilityExists(formErrors, facilityMotechId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.FACILITY_ID, NOT_FOUND)));

        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(facility);
        formErrors = new ArrayList<FormError>();
        formValidator.validateIfFacilityExists(formErrors, facilityMotechId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.FACILITY_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidateIfPatientExists() {
        String patientId = "patientId";
        when(patientService.getPatientByMotechId(patientId)).thenReturn(null);
        ArrayList<FormError> formErrors = new ArrayList<FormError>();
        formValidator.validatePatient(formErrors, patientId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.PATIENT_ID, NOT_FOUND)));

        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(patientId, false);
        formErrors = new ArrayList<FormError>();
        formValidator.validatePatient(formErrors, patientId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.PATIENT_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidateIfStaffExists(){
        String staffId = "1234567";
        when(staffService.getUserById(staffId)).thenReturn(null);

        ArrayList<FormError> formErrors = new ArrayList<FormError>();
        formValidator.validateIfStaffExists(formErrors, staffId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.STAFF_ID, NOT_FOUND)));

        MRSUser staff = mock(MRSUser.class);
        when(staffService.getUserById(staffId)).thenReturn(staff);

        formValidator.validateIfStaffExists(formErrors, staffId);
        formErrors = new ArrayList<FormError>();
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.STAFF_ID, NOT_FOUND))));
    }
}
