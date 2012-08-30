package org.motechproject.ghana.national.service;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.*;
import org.motechproject.util.DateUtil;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.IdentifierNotUniqueException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.EncounterType.PATIENT_EDIT_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.PATIENT_REG_VISIT;

public class PatientServiceTest {

    PatientService patientService;
    @Mock
    private AllPatients mockAllPatients;
    @Mock
    private IdentifierGenerator mockIdentifierGenerator;
    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private AllSchedulesAndMessages mockAllSchedulesAndSchedules;
    @Mock
    private AllAppointmentsAndMessages mockAllAppointmentsAndMessages;
    @Mock
    private AllPatientSearch mockAllPatientSearch;
    @Mock
    private AllMobileMidwifeEnrollments mockAllMobileMidwifeEnrollments;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(mockAllPatients, mockIdentifierGenerator, mockAllEncounters, mockAllSchedulesAndSchedules, mockAllAppointmentsAndMessages, mockAllPatientSearch, mockAllMobileMidwifeEnrollments);
    }

    @Test
    public void shouldNotTryToValidateParentIfParentIdIsNotSet() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        final String parentId = "";
        Patient patient = new Patient(new MRSPatient("100", "1234343", new MRSPerson(), new MRSFacility("34")), parentId);
        MRSEncounter mrsEncounter = mock(MRSEncounter.class);
        String staffId = "1234";
        Patient savedPatient = mock(Patient.class);
        MRSPatient mrsPatient = mock(MRSPatient.class);
        when(savedPatient.getMrsPatient()).thenReturn(mrsPatient);
        when(mockAllPatients.save(patient)).thenReturn(savedPatient);
        when(mrsPatient.getMotechId()).thenReturn("100");
        when(mockAllEncounters.persistEncounter(mrsPatient, staffId, "34", PATIENT_REG_VISIT.value(), new Date(), null)).thenReturn(mrsEncounter);
        patientService.registerPatient(patient, "1234", null);
        verify(mockAllPatients, times(0)).getPatientByMotechId(parentId);
    }

    @Test(expected = ParentNotFoundException.class)
    public void shouldThrowParentNotFoundExceptionWhenNotFound() throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        Patient patient = mock(Patient.class);
        final String parentId = "11";
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getMotechId()).thenReturn("32");
        when(patient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(patient.getParentId()).thenReturn(parentId);
        doThrow(new ParentNotFoundException()).when(mockAllPatients).save(patient);
        patientService.registerPatient(patient, "1234", null);
    }

    @Test
    public void shouldGeneratePatientMotechIdIfNotProvided() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        String patientId = "patient id";
        String patientMotechId = "patient Motech id";

        when(mockIdentifierGenerator.newPatientId()).thenReturn(patientMotechId);

        MRSPerson person = mock(MRSPerson.class);
        MRSFacility facility = mock(MRSFacility.class);

        MRSPatient mrsPatient = new MRSPatient(patientId, patientMotechId, person, facility);
        Patient patient = new Patient(mrsPatient, null);

        when(mockAllPatients.save(patient)).thenReturn(patient);
        patientService.registerPatient(patient, "1234", null);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(mockAllPatients).save(patientArgumentCaptor.capture());

        Patient patientPassedToAllPatientsService = patientArgumentCaptor.getValue();
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getMotechId(), is(equalTo(patientMotechId)));
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getPerson(), is(equalTo(person)));
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getFacility(), is(equalTo(facility)));
        assertThat(patientPassedToAllPatientsService.getMrsPatient().getId(), is(equalTo(patientId)));

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
        when(mockAllPatients.getPatientByMotechId(parentId)).thenReturn(mother);
        doThrow(new IdentifierNotUniqueException()).when(mockAllPatients).save(patient);
        patientService.registerPatient(patient, "1234", null);
    }

    @Test
    public void shouldReturnGetAPatientById() {
        String patientId = "10000";
        Patient patient = mock(Patient.class);
        MRSPerson mockPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(patient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockAllPatients.getMotherRelationship(mockPerson)).thenReturn(null);
        when(mockAllPatients.getPatientByMotechId(patientId)).thenReturn(patient);
        assertThat(patientService.getPatientByMotechId(patientId), is(equalTo(patient)));

        when(mockAllPatients.getPatientByMotechId(patientId)).thenReturn(null);
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
    public void shouldCreateMotherChildRelationshipAndUpdatePatient() throws ParentNotFoundException {
        String parentMotechId = "123";
        String savedChildMotechId = "234";
        String facilityId = "13161";
        MRSFacility mrsFacility = new MRSFacility(facilityId);
        final MRSPerson parentPerson = new MRSPerson();
        Patient mockParent = new Patient(new MRSPatient("", parentMotechId, parentPerson, mrsFacility));
        MRSPerson childPerson = new MRSPerson();
        MRSPatient mrsPatient = new MRSPatient("", savedChildMotechId, childPerson, mrsFacility);
        Patient childPatient = new Patient(mrsPatient).parentId(parentMotechId);
        when(mockAllPatients.update(childPatient)).thenReturn(childPatient);
        when(mockAllPatients.getPatientByMotechId(parentMotechId)).thenReturn(mockParent);
        when(mockAllPatients.getPatientByMotechId(savedChildMotechId)).thenReturn(childPatient);
        when(mockAllPatients.getMotherRelationship(childPerson)).thenReturn(null);

        final String staffId = "1234";
        Date updatedDate = new Date();
        patientService.updatePatient(childPatient, staffId, updatedDate);

        verify(mockAllPatients).update(childPatient);
        verify(mockAllPatients).createMotherChildRelationship(parentPerson, childPerson);
        verify(mockAllEncounters).persistEncounter(refEq(mrsPatient), eq(staffId), eq(facilityId), eq(PATIENT_EDIT_VISIT.value()), eq(updatedDate),
                org.mockito.Matchers.<Set<MRSObservation>>any());
    }

    @Test
    public void shouldVoidMotherChildRelationshipAndUpdate() throws ParentNotFoundException {
        Patient mockPatient = mock(Patient.class);
        String parentId = "";
        String savedPatientId = "234";
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockAllPatients.update(mockPatient)).thenReturn(mockPatient);
        MRSPerson mockPerson = mock(MRSPerson.class);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getPerson()).thenReturn(mockPerson);
        Relationship mockRelationship = mock(Relationship.class);
        when(mockAllPatients.getMotherRelationship(mockPerson)).thenReturn(mockRelationship);
        final PatientService spyPatientService = spy(patientService);
        doReturn(mockPatient).when(spyPatientService).getPatientByMotechId(savedPatientId);
        doNothing().when(spyPatientService).createRelationship(parentId, savedPatientId);

        MRSFacility mockMRSFacility = mock(MRSFacility.class);
        when(mockMRSPatient.getFacility()).thenReturn(mockMRSFacility);
        String facilityId = "13161";
        when(mockMRSFacility.getId()).thenReturn(facilityId);
        String staffId = "468";
        MRSEncounter mockMRSEncounter = mock(MRSEncounter.class);
        Date dateOfEncounter = new Date();
        when(mockAllEncounters.persistEncounter(mockMRSPatient, staffId, facilityId, PATIENT_REG_VISIT.value(), dateOfEncounter, null)).thenReturn(mockMRSEncounter);

        spyPatientService.updatePatient(mockPatient, staffId, dateOfEncounter);
        verify(mockAllPatients).update(mockPatient);
        verify(mockAllPatients).voidMotherChildRelationship(mockPerson);
    }

    @Test
    public void shouldUpdateMotherChildRelationshipAndUpdate() throws ParentNotFoundException {
        String parentId = "parentId";
        String savedPatientId = "234";

        Patient mockPatient = mock(Patient.class);
        when(mockAllPatients.update(mockPatient)).thenReturn(mockPatient);
        MRSPerson mockChildPerson = mock(MRSPerson.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockPatient.getParentId()).thenReturn(parentId);
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
        MRSFacility mockMRSFacility = mock(MRSFacility.class);
        when(mockMRSPatient.getFacility()).thenReturn(mockMRSFacility);
        String facilityId = "13161";
        when(mockMRSFacility.getId()).thenReturn(facilityId);
        String staffId = "468";
        MRSEncounter mockMRSEncounter = mock(MRSEncounter.class);
        Date dateOfEncounter = new Date();
        when(mockAllEncounters.persistEncounter(mockMRSPatient, staffId, facilityId, PATIENT_REG_VISIT.value(), dateOfEncounter, null)).thenReturn(mockMRSEncounter);

        final PatientService spyPatientService = spy(patientService);
        doReturn(mockPatient).when(spyPatientService).getPatientByMotechId(savedPatientId);
        doReturn(mockMotherPatient).when(spyPatientService).getPatientByMotechId(parentId);
        doNothing().when(spyPatientService).createRelationship(parentId, savedPatientId);

        spyPatientService.updatePatient(mockPatient, "", dateOfEncounter);

        verify(mockAllPatients).update(mockPatient);
        verify(mockAllPatients).updateMotherChildRelationship(mockMotherPerson, mockChildPerson);
    }

    @Test
    public void shouldGetAgeOfAPerson() {
        String motechId = "12345";
        patientService.getAgeOfPatientByMotechId(motechId);
        verify(mockAllPatients).getAgeOfPersonByMotechId(motechId);
    }

    @Test
    public void shouldDeceasePatient() throws PatientNotFoundException {
        Date dateOfDeath = DateUtil.now().minusDays(2).toDate();
        String patientMotechId = "patientMotechId";
        String patientMRSId = "patientId";
        String causeOfDeath = "OTHER";
        String comment = null;
        final Patient patient = spy(new Patient(new MRSPatient(patientMRSId)));
        List<String> schedules = asList("schedule1", "schedule2");
        doReturn(schedules).when(patient).allCareProgramsToUnEnroll();
        when(mockAllPatients.getPatientByMotechId(patientMotechId)).thenReturn(patient);

        patientService.deceasePatient(dateOfDeath, patientMotechId, causeOfDeath, comment);

        verify(mockAllPatients).deceasePatient(dateOfDeath, patientMotechId, "OTHER NON-CODED", comment);
        verify(mockAllSchedulesAndSchedules).unEnroll(patientMRSId, schedules);
        verify(mockAllAppointmentsAndMessages).remove(patient);
    }

    @Test
    public void shouldSearchPatientGivenInfoOtherThanMotechId(){
        String firstName = "firstNm";
        String phoneNumber = "phnm";
        Date dateOfBirth = new Date(2009, 9, 9);
        patientService.getPatients(firstName,null, phoneNumber, dateOfBirth,null);
        verify(mockAllPatientSearch).getPatients(firstName,null,phoneNumber,dateOfBirth,null);
    }

    @Test
    public void shouldReturnPhoneNumberRegisteredWithMobileMidwifeIfThePatientHasActiveEnrollment() {
        String phoneNumber = "919500012123";
        String mobileMidwifePhoneNumber = "919544412111";
        String motechId = "motechId";
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().attributes(Arrays.asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber))), new MRSFacility("facilityid")));
        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(DateTime.now());
        mobileMidwifeEnrollment.setPhoneNumber(mobileMidwifePhoneNumber);
        when(mockAllPatients.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motechId)).thenReturn(mobileMidwifeEnrollment);

        Assert.assertThat(patientService.receiveSMSOnPhoneNumber(motechId), Is.is(equalTo(mobileMidwifePhoneNumber)));
    }

    @Test
    public void shouldReturnPhoneNumberRegisteredWithMotherMobileMidwifeIfThePatientHasNoActiveEnrollment() {
        String phoneNumber = "919500012123";
        String mobileMidwifePhoneNumber = "919544412111";
        String motechId = "motechId";
        String motherMotechId="motherMotechId";
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().attributes(Arrays.asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber))), new MRSFacility("facilityid")));
        Patient mother = new Patient(new MRSPatient(motherMotechId, new MRSPerson().attributes(Arrays.asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber))), new MRSFacility("facilityid")));
        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(DateTime.now());
        mobileMidwifeEnrollment.setPhoneNumber(mobileMidwifePhoneNumber);

        when(mockAllPatients.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motechId)).thenReturn(null);
        when(mockAllPatients.getMother(motechId)).thenReturn(mother);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motherMotechId)).thenReturn(mobileMidwifeEnrollment);

        Assert.assertThat(patientService.receiveSMSOnPhoneNumber(motechId), Is.is(equalTo(mobileMidwifePhoneNumber)));

    }


    @Test
    public void shouldReturnPatientPhoneNumberIfBothPatientAndMotherHasNoMobileMidwifeEnrollment() {
        String phoneNumber = "919500012123";
        String motechId = "motechId";
        String motherMotechId="motherMotechId";
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().attributes(Arrays.asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber))), new MRSFacility("facilityid")));
        Patient mother = new Patient(new MRSPatient(motherMotechId, new MRSPerson(), new MRSFacility("facilityid")));

        when(mockAllPatients.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motechId)).thenReturn(null);
        when(mockAllPatients.getMother(motechId)).thenReturn(mother);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motherMotechId)).thenReturn(null);

        Assert.assertThat(patientService.receiveSMSOnPhoneNumber(motechId), Is.is(equalTo(phoneNumber)));

    }

    @Test
    public void shouldReturnMotherPhoneNumberIfBothPatientAndMotherHasNoMobileMidwifeEnrollmentAndPatientHasNoPhoneNumber() {
        String phoneNumber = "919500012123";
        String motechId = "motechId";
        String motherMotechId="motherMotechId";
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson(), new MRSFacility("facilityid")));
        Patient mother = new Patient(new MRSPatient(motherMotechId, new MRSPerson().attributes(Arrays.asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber))), new MRSFacility("facilityid")));

        when(mockAllPatients.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motechId)).thenReturn(null);
        when(mockAllPatients.getMother(motechId)).thenReturn(mother);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(motherMotechId)).thenReturn(null);

        Assert.assertThat(patientService.receiveSMSOnPhoneNumber(motechId), Is.is(equalTo(phoneNumber)));

    }

}
