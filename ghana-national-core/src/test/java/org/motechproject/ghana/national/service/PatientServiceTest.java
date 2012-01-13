package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.IdentifierNotUniqueException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {

    PatientService patientService;
    @Mock
    private AllPatients mockAllPatients;
    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private IdentifierGenerationService mockIdentifierGenerationService;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(mockAllPatients, mockAllEncounters , mockIdentifierGenerationService);
    }

    @Test
    public void shouldNotTryToValidateParentIfParentIdIsNotSet() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        final String parentId = "";
        Patient patient = new Patient(new MRSPatient("100"), parentId);
        patientService.registerPatient(patient);
        verify(mockAllPatients, times(0)).patientByMotechId(parentId);
    }

    @Test(expected = ParentNotFoundException.class)
    public void shouldThrowParentNotFoundExceptionWhenNotFound() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        final String parentId = "11";
        when(mockAllPatients.patientByMotechId(parentId)).thenReturn(null);
        MRSPerson mockPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(patient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(patient.getParentId()).thenReturn(parentId);
        when(mockAllPatients.getMotherRelationship(mockPerson)).thenReturn(null);
        patientService.registerPatient(patient);
    }
    @Test
    public void shouldGeneratePatientMotechIdIfNotProvided() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        String patientId = "patient id";
        String patientMotechId = "patient Motech id";

        when(mockIdentifierGenerationService.newPatientId()).thenReturn(patientMotechId);

        MRSPerson person = mock(MRSPerson.class);
        MRSFacility facility = mock(MRSFacility.class);

        MRSPatient mrsPatient = new MRSPatient(patientId, null, person, facility);
        Patient patient = new Patient(mrsPatient, null);

        patientService.registerPatient(patient);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(mockAllPatients).save(patientArgumentCaptor.capture());

        Patient patientPassedToAllPatientsService = patientArgumentCaptor.getValue();
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getMotechId(), is(equalTo(patientMotechId)));
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getPerson(), is(equalTo(person)));
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getFacility(), is(equalTo(facility)));
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getId(), is(equalTo(patientId)));

    }

    @Test
    public void shouldRegisterAPatientWithParentIfFound() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        final String parentId = "11";
        Patient childBeforeRegistering = mock(Patient.class);
        MRSPatient mrsChildPatientBeforeRegistering = mock(MRSPatient.class);
        String motechId = "motechID";
        when(mrsChildPatientBeforeRegistering.getMotechId()).thenReturn(motechId);
        when(childBeforeRegistering.getMrsPatient()).thenReturn(mrsChildPatientBeforeRegistering);
        when(childBeforeRegistering.getParentId()).thenReturn(parentId);

        Patient mother = mock(Patient.class);
        final MRSPatient mrsMother = mock(MRSPatient.class);
        final MRSPerson mrsMotherPerson = mock(MRSPerson.class);
        when(mother.getMrsPatient()).thenReturn(mrsMother);
        when(mrsMother.getPerson()).thenReturn(mrsMotherPerson);
        when(mockAllPatients.patientByMotechId(parentId)).thenReturn(mother);

        Patient child = mock(Patient.class);
        final MRSPatient mrsChild = mock(MRSPatient.class);
        final MRSPerson mrsChildPerson = mock(MRSPerson.class);
        when(child.getMrsPatient()).thenReturn(mrsChild);
        when(mrsChild.getPerson()).thenReturn(mrsChildPerson);
        String childId = "121";
        when(mockAllPatients.save(childBeforeRegistering)).thenReturn(childId);
        when(mockAllPatients.patientByMotechId(childId)).thenReturn(child);
        patientService.registerPatient(childBeforeRegistering);

        verify(mockAllPatients).save(childBeforeRegistering);
        verify(mockAllPatients).createMotherChildRelationship(mrsMotherPerson, mrsChildPerson);
    }

    @Test(expected = PatientIdNotUniqueException.class)
    public void shouldThrowPatientIdNotUniqueExceptionWhenOpenMRSThrowsCorrespondingException() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        Patient mother = mock(Patient.class);
        final String parentId = "11";
        when(patient.getParentId()).thenReturn(parentId);
        MRSPatient mrsPatient = mock(MRSPatient.class);
        when(mrsPatient.getMotechId()).thenReturn("motechId");
        when(patient.getMrsPatient()).thenReturn(mrsPatient);
        when(mockAllPatients.patientByMotechId(parentId)).thenReturn(mother);
        doThrow(new IdentifierNotUniqueException()).when(mockAllPatients).save(patient);
        patientService.registerPatient(patient);
    }

    @Test
    public void shouldReturnGetAPatientById() {
        String patientId = "10000";
        Patient patient = mock(Patient.class);
        MRSPerson mockPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(patient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockAllPatients.getMotherRelationship(mockPerson)).thenReturn(null);
        when(mockAllPatients.patientByMotechId(patientId)).thenReturn(patient);
        assertThat(patientService.getPatientByMotechId(patientId), is(equalTo(patient)));

        when(mockAllPatients.patientByMotechId(patientId)).thenReturn(null);
        assertThat(patientService.getPatientByMotechId(patientId), is(equalTo(null)));
    }

    @Test
    public void shouldSearchPatientByNameOrId() {
        List<Patient> patientList = Arrays.asList(mock(Patient.class));
        String motechId = "123456";
        String name = "name";
        when(mockAllPatients.search(name, motechId)).thenReturn(patientList);
        assertThat(patientService.search(name, motechId), is(equalTo(patientList)));

        when(mockAllPatients.search(name, null)).thenReturn(patientList);
        assertThat(patientService.search(name, ""), is(equalTo(patientList)));

    }

    @Test
    public void shouldCreateMotherChildRelationshipAndUpdate() throws ParentNotFoundException {
        Patient mockPatient = mock(Patient.class);
        String parentId = "123";
        String savedPatientId = "234";
        when(mockAllPatients.update(mockPatient)).thenReturn(savedPatientId);
        MRSPerson mockPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getPerson()).thenReturn(mockPerson);
        when(mockAllPatients.getMotherRelationship(mockPerson)).thenReturn(null);
        final PatientService spyPatientService = spy(patientService);
        doReturn(mockPatient).when(spyPatientService).getPatientByMotechId(savedPatientId);
        doNothing().when(spyPatientService).createRelationship(parentId, savedPatientId);

        spyPatientService.updatePatient(mockPatient, parentId);

        verify(mockAllPatients).update(mockPatient);
        verify(spyPatientService).createRelationship(parentId, savedPatientId);
    }

    @Test
    public void shouldVoidMotherChildRelationshipAndUpdate() throws ParentNotFoundException {
        Patient mockPatient = mock(Patient.class);
        String parentId = "";
        String savedPatientId = "234";
        when(mockAllPatients.update(mockPatient)).thenReturn(savedPatientId);
        MRSPerson mockPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getPerson()).thenReturn(mockPerson);
        Relationship mockRelationship = mock(Relationship.class);
        when(mockAllPatients.getMotherRelationship(mockPerson)).thenReturn(mockRelationship);
        final PatientService spyPatientService = spy(patientService);
        doReturn(mockPatient).when(spyPatientService).getPatientByMotechId(savedPatientId);
        doNothing().when(spyPatientService).createRelationship(parentId, savedPatientId);

        spyPatientService.updatePatient(mockPatient, parentId);
        verify(mockAllPatients).update(mockPatient);
        verify(mockAllPatients).voidMotherChildRelationship(mockPerson);
    }

    @Test
    public void shouldUpdateMotherChildRelationshipAndUpdate() throws ParentNotFoundException {
        String parentId = "parentId";
        String savedPatientId = "234";

        Patient mockPatient = mock(Patient.class);
        when(mockAllPatients.update(mockPatient)).thenReturn(savedPatientId);
        MRSPerson mockChildPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getPerson()).thenReturn(mockChildPerson);

        Relationship mockRelationship = mock(Relationship.class);
        final Person mockPersonA = mock(Person.class);
        when(mockRelationship.getPersonA()).thenReturn(mockPersonA);
        when(mockAllPatients.getMotherRelationship(mockChildPerson)).thenReturn(mockRelationship);

        MRSPerson mockMotherPerson = mock(MRSPerson.class);
        MRSPatient mockMotherMRSPatient = mock(MRSPatient.class);
        Patient mockMotherPatient = mock(Patient.class);
        when(mockMotherPatient.getMrsPatient()).thenReturn(mockMotherMRSPatient);
        when(mockMotherMRSPatient.getPerson()).thenReturn(mockMotherPerson);

        final PatientService spyPatientService = spy(patientService);
        doReturn(mockPatient).when(spyPatientService).getPatientByMotechId(savedPatientId);
        doReturn(mockMotherPatient).when(spyPatientService).getPatientByMotechId(parentId);
        doNothing().when(spyPatientService).createRelationship(parentId, savedPatientId);

        spyPatientService.updatePatient(mockPatient, parentId);

        verify(mockAllPatients).update(mockPatient);
        verify(mockAllPatients).updateMotherChildRelationship(mockMotherPerson, mockChildPerson);
    }
    
    @Test
    public void shouldGetAgeOfAPerson(){
        String motechId = "12345";
        patientService.getAgeOfPatientByMotechId(motechId);
        verify(mockAllPatients).getAgeOfPersonByMotechId(motechId);
    }

    @Test
    public void shouldSaveEncounter() {
        MRSEncounter mrsEncounter = mock(MRSEncounter.class);
        patientService.saveEncounter(mrsEncounter);
        verify(mockAllEncounters).save(mrsEncounter);
    }

}
