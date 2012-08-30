package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.services.OpenMRSRelationshipAdapter;
import org.motechproject.util.DateUtil;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllPatientsTest {

    AllPatients allPatients;

    @Mock
    MRSPatientAdapter mockMrsPatientAdapter;
    @Mock
    OpenMRSRelationshipAdapter mockOpenMRSRelationshipAdapter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allPatients = new AllPatients();
        ReflectionTestUtils.setField(allPatients, "patientAdapter", mockMrsPatientAdapter);
        ReflectionTestUtils.setField(allPatients, "openMRSRelationshipAdapter", mockOpenMRSRelationshipAdapter);
    }

    @Test
    public void shouldSaveAPatientWithRelationship() {
        MRSFacility facility = new MRSFacility("1", "facility", "country", "region", "district", "state");
        String first = "first";
        String middle = "middle";
        String last = "last";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientId = "1000";
        String parentId = "parentId";
        Boolean birthDateEstimated = true;
        String motherId = "motherMotechId";
        String motherPersonId = "1";
        MRSPatient mrsParent = new MRSPatient(motherId, new MRSPerson().id(motherPersonId), null);

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        final MRSPatient mrsPatient = new MRSPatient("", mrsPerson, facility);
        final Patient patient = new Patient(mrsPatient, parentId);
        String childPersonId = "2";
        MRSPatient savedPatient = new MRSPatient(patientId, new MRSPerson().id(childPersonId), null);

        when(mockMrsPatientAdapter.getPatientByMotechId(parentId)).thenReturn(mrsParent);
        when(mockMrsPatientAdapter.savePatient(mrsPatient)).thenReturn(savedPatient);

        final String savedMotechId = allPatients.save(patient).getMotechId();

        verify(mockOpenMRSRelationshipAdapter).createMotherChildRelationship(motherPersonId, childPersonId);
        verify(mockMrsPatientAdapter).savePatient(mrsPatient);
        assertThat(patientId, is(savedMotechId));
    }

    @Test(expected = ParentNotFoundException.class)
    public void shouldThrowExceptionIfParentIsNotFound() {
        MRSFacility facility = new MRSFacility("1", "facility", "country", "region", "district", "state");
        String first = "first";
        String middle = "middle";
        String last = "last";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientId = "1000";
        String parentId = "parentId";
        Boolean birthDateEstimated = true;
        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        final MRSPatient mrsPatient = new MRSPatient("", mrsPerson, facility);
        final Patient patient = new Patient(mrsPatient, parentId);
        String childPersonId = "2";
        MRSPatient savedPatient = new MRSPatient(patientId, new MRSPerson().id(childPersonId), null);

        when(mockMrsPatientAdapter.getPatientByMotechId(parentId)).thenReturn(null);
        when(mockMrsPatientAdapter.savePatient(mrsPatient)).thenReturn(savedPatient);

        allPatients.save(patient).getMotechId();
    }

    @Test
    public void shouldReturnNullIfFetchPatientByIdIsNull() {
        final String patientId = "1";
        when(mockMrsPatientAdapter.getPatientByMotechId(patientId)).thenReturn(null);
        final Patient actualPatient = allPatients.getPatientByMotechId(patientId);
        assertNull(actualPatient);
    }

    @Test
    public void shouldReturnMotechIdByPatientId() {
        String patientId = "123";
        final MRSPatient mrsPatient = mock(MRSPatient.class);
        when(mockMrsPatientAdapter.getPatient(patientId)).thenReturn(mrsPatient);
        Patient actualPatient = allPatients.patientByOpenmrsId(patientId);
        assertThat(actualPatient.getMrsPatient(), is(equalTo(mrsPatient)));
    }

    @Test
    public void shouldReturnNullForMotechIdByPatientIdIfNotFound() {
        String patientId = "123";
        when(mockMrsPatientAdapter.getPatient(patientId)).thenReturn(null);
        Patient actualPatient = allPatients.patientByOpenmrsId(patientId);
        assertNull(actualPatient);
    }

    @Test
    public void shouldFetchPatientById() {
        final String patientId = "1";
        String personId = "2";
        MRSPatient patient = new MRSPatient(patientId, new MRSPerson().id(personId), null);
        when(mockMrsPatientAdapter.getPatientByMotechId(patientId)).thenReturn(patient);
        when(mockOpenMRSRelationshipAdapter.getMotherRelationship(personId)).thenReturn(null);
        final Patient actualPatient = allPatients.getPatientByMotechId(patientId);
        assertThat(actualPatient.getMrsPatient(), is(patient));
    }

    @Test
    public void shouldFetchPatientByIdAndPopulateRelationshipIfFound() {
        final String patientId = "1";
        String personId = "2";
        MRSPerson child = new MRSPerson().id(personId);
        MRSPatient childPatient = new MRSPatient(patientId, child, null);
        when(mockMrsPatientAdapter.getPatientByMotechId(patientId)).thenReturn(childPatient);
        Relationship motherRelationship = new Relationship(122);
        final int motherPersonId = 123;
        Person mother = new Person(motherPersonId);
        PersonName motherName = new PersonName("given", null, "family");
        mother.addName(motherName);
        motherRelationship.setPersonA(mother);
        final String motherMotechId = "111";

        when(mockMrsPatientAdapter.search(motherName.getFullName(), null)).thenReturn(new ArrayList<MRSPatient>() {{
            add(new MRSPatient(motherMotechId, new MRSPerson().id(String.valueOf(motherPersonId)), null));
        }});
        when(mockOpenMRSRelationshipAdapter.getMotherRelationship(personId)).thenReturn(motherRelationship);

        final Patient actualPatient = allPatients.getPatientByMotechId(patientId);

        assertThat(actualPatient.getMrsPatient(), is(childPatient));
        assertThat(actualPatient.getParentId(), is(motherMotechId));
    }

    @Test
    public void shouldSearchPatientByNameOrId() {
        String name = "name";
        String motechId = "id";
        MRSPatient returnedMrsPatient = new MRSPatient(motechId);
        List<MRSPatient> returnedMrsPatientList = Arrays.asList(returnedMrsPatient);
        when(mockMrsPatientAdapter.search(name, motechId)).thenReturn(returnedMrsPatientList);
        List<Patient> returnedPatient = allPatients.search(name, motechId);
        assertThat(returnedPatient.size(), is(equalTo(1)));
        assertThat(returnedPatient.get(0).getMrsPatient(), is(equalTo(returnedMrsPatient)));

    }

    @Test
    public void shouldUpdateAPatient() {
        MRSFacility facility = new MRSFacility("1", "facility", "country", "region", "district", "state");
        String first = "first";
        String middle = "middle";
        String last = "last";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientMotechId = "1000";
        Boolean birthDateEstimated = true;

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        final MRSPatient mrsPatient = new MRSPatient(patientMotechId, mrsPerson, facility);
        final Patient patient = new Patient(mrsPatient);
        when(mockMrsPatientAdapter.updatePatient(patient.getMrsPatient())).thenReturn(mrsPatient);
        final Patient updatedPatient = allPatients.update(patient);
        assertThat(patientMotechId, is(updatedPatient.getMotechId()));
    }

    @Test
    public void shouldCreateMotherRelation() {
        MRSPerson mockMother = mock(MRSPerson.class);
        MRSPerson mockChild = mock(MRSPerson.class);
        final String motherId = "123";
        String childId = "234";
        when(mockMother.getId()).thenReturn(motherId);
        when(mockChild.getId()).thenReturn(childId);
        allPatients.createMotherChildRelationship(mockMother, mockChild);
        verify(mockOpenMRSRelationshipAdapter).createMotherChildRelationship(motherId, childId);
    }

    @Test
    public void shouldUpdateMotherRelation() {
        MRSPerson mockMother = mock(MRSPerson.class);
        MRSPerson mockChild = mock(MRSPerson.class);
        final String motherId = "123";
        String childId = "234";
        when(mockMother.getId()).thenReturn(motherId);
        when(mockChild.getId()).thenReturn(childId);
        allPatients.updateMotherChildRelationship(mockMother, mockChild);
        verify(mockOpenMRSRelationshipAdapter).updateMotherRelationship(motherId, childId);
    }

    @Test
    public void shouldVoidMotherChildRelationship() {
        MRSPerson mockChild = mock(MRSPerson.class);
        String childId = "234";
        when(mockChild.getId()).thenReturn(childId);
        allPatients.voidMotherChildRelationship(mockChild);
        verify(mockOpenMRSRelationshipAdapter).voidRelationship(childId);
    }

    @Test
    public void shouldGetMotherRelationship() {
        MRSPerson mockChild = mock(MRSPerson.class);
        String childId = "234";
        when(mockChild.getId()).thenReturn(childId);
        allPatients.getMotherRelationship(mockChild);
        verify(mockOpenMRSRelationshipAdapter).getMotherRelationship(childId);
    }

    @Test
    public void shouldGetAgeOfAPerson() {
        String motechId = "1234567";

        when(mockMrsPatientAdapter.getAgeOfPatientByMotechId(motechId)).thenReturn(3);
        allPatients.getAgeOfPersonByMotechId(motechId);

        verify(mockMrsPatientAdapter).getAgeOfPatientByMotechId(motechId);
    }

    @Test
    public void shouldSaveCauseOfDeath() throws PatientNotFoundException {
        String mrsPatientId = "patientId";
        Date dateOfDeath = DateUtil.now().toDate();
        String causeOfDeath = "NONE";
        String comment = null;
        allPatients.deceasePatient(dateOfDeath, mrsPatientId, causeOfDeath, comment);
        verify(mockMrsPatientAdapter).deceasePatient(mrsPatientId, causeOfDeath, dateOfDeath, comment);
    }
    
    @Test
    public void shouldReturnMotherGivenMotechIdOfAPatient() {

        String motechId = "motechId";
        String motherMotechId ="motherMotechId";
        String motherPersonId = "1111";
        String childPersonId = "2222";
        MRSPerson personMother = new MRSPerson().id(motherPersonId);
        MRSPerson personChild = new MRSPerson().id(childPersonId);
        Patient patient=new Patient(new MRSPatient(childPersonId, personChild, new MRSFacility("facilityid")));
        Patient expectedMother=new Patient(new MRSPatient(motherMotechId, personMother, new MRSFacility("facilityid")));
        Relationship mockMotherChildRelationShip =new Relationship(new Person(Integer.parseInt(motherPersonId)),new Person(Integer.parseInt(childPersonId)),new RelationshipType(3));

        when(mockMrsPatientAdapter.getPatientByMotechId(motechId)).thenReturn(patient.getMrsPatient());
        when(mockMrsPatientAdapter.getPatient(motherPersonId)).thenReturn(expectedMother.getMrsPatient());
        when(mockOpenMRSRelationshipAdapter.getMotherRelationship(childPersonId)).thenReturn(mockMotherChildRelationShip);

        Patient actualMother = allPatients.getMother(motechId);
        assertThat(actualMother.getMotechId(),is(equalTo(expectedMother.getMotechId())));
        assertThat(actualMother.getMRSPatientId(),is(equalTo(expectedMother.getMRSPatientId())));
    }
}
