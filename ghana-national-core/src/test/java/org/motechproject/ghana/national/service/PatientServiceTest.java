package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.AllPatients;
import org.openmrs.api.IdentifierNotUniqueException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {

    PatientService patientService;
    @Mock
    private AllPatients mockAllPatients;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(mockAllPatients);
    }

    @Test
    public void shouldNotTryToValidateParentIfParentIdIsNotSet() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        final String parentId = "";
        patientService.registerPatient(patient, PatientType.CHILD_UNDER_FIVE, parentId);
        verify(mockAllPatients, times(0)).patientById(parentId);
    }

    @Test(expected = ParentNotFoundException.class)
    public void shouldThrowParentNotFoundExceptionWhenNotFound() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        final String parentId = "11";
        when(mockAllPatients.patientById(parentId)).thenReturn(null);
        patientService.registerPatient(patient, PatientType.CHILD_UNDER_FIVE, parentId);
    }

    @Test
    public void shouldRegisterAPatientWithParentIfFound() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        Patient mother = mock(Patient.class);
        final String parentId = "11";
        when(mockAllPatients.patientById(parentId)).thenReturn(mother);
        patientService.registerPatient(patient, PatientType.CHILD_UNDER_FIVE, parentId);

        verify(mockAllPatients).add(patient);
    }

    @Test(expected = PatientIdNotUniqueException.class)
    public void shouldThrowPatientIdNotUniqueExceptionWhenOpenMRSThrowsCorrespondingException() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        Patient mother = mock(Patient.class);
        final String parentId = "11";
        when(mockAllPatients.patientById(parentId)).thenReturn(mother);
        doThrow(new IdentifierNotUniqueException()).when(mockAllPatients).add(patient);
        patientService.registerPatient(patient, PatientType.CHILD_UNDER_FIVE, parentId);
    }
}
