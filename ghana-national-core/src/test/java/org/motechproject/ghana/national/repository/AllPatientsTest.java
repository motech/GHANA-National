package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.services.OpenMRSRelationshipAdaptor;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

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
    MRSPatientAdaptor mockMrsPatientAdaptor;
    @Mock
    OpenMRSRelationshipAdaptor mockOpenMRSRelationshipAdaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allPatients = new AllPatients();
        ReflectionTestUtils.setField(allPatients, "patientAdaptor", mockMrsPatientAdaptor);
        ReflectionTestUtils.setField(allPatients, "openMRSRelationshipAdaptor", mockOpenMRSRelationshipAdaptor);
    }

    @Test
    public void shouldSaveAPatient() {
        MRSFacility facility = new MRSFacility("1", "facility", "country", "region", "district", "state");
        String first = "first";
        String middle = "middle";
        String last = "last";
        String preferred = "preferred";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientId = "1000";
        Boolean birthDateEstimated = true;

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last).preferredName(preferred)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        final MRSPatient mrsPatient = new MRSPatient("", mrsPerson, facility);
        final Patient patient = new Patient(mrsPatient);
        MRSPatient savedPatient = mock(MRSPatient.class);
        when(savedPatient.getMotechId()).thenReturn(patientId);
        when(mockMrsPatientAdaptor.savePatient(mrsPatient)).thenReturn(savedPatient);
        final String savedMotechId = allPatients.save(patient);
        verify(mockMrsPatientAdaptor).savePatient(mrsPatient);
        assertThat(patientId, is(savedMotechId));
    }

    @Test
    public void shouldReturnNullIfFetchPatientByIdIsNull() {
        final String patientId = "1";
        when(mockMrsPatientAdaptor.getPatientByMotechId(patientId)).thenReturn(null);
        final Patient actualPatient = allPatients.patientByMotechId(patientId);
        assertNull(actualPatient);
    }

    @Test
    public void shouldFetchPatientById() {
        final String patientId = "1";
        MRSPatient patient = new MRSPatient(patientId);
        when(mockMrsPatientAdaptor.getPatientByMotechId(patientId)).thenReturn(patient);
        final Patient actualPatient = allPatients.patientByMotechId(patientId);
        assertThat(actualPatient.getMrsPatient(), is(patient));
    }

    @Test
    public void shouldSearchPatientByNameOrId() {
        String name = "name";
        String motechId = "id";
        MRSPatient returnedMrsPatient = new MRSPatient(motechId);
        List<MRSPatient> returnedMrsPatientList = Arrays.asList(returnedMrsPatient);
        when(mockMrsPatientAdaptor.search(name, motechId)).thenReturn(returnedMrsPatientList);
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
        String preferred = "preferred";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientId = "1000";
        Boolean birthDateEstimated = true;

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last).preferredName(preferred)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        final MRSPatient mrsPatient = new MRSPatient("", mrsPerson, facility);
        final Patient patient = new Patient(mrsPatient);
        when(mockMrsPatientAdaptor.updatePatient(patient.getMrsPatient())).thenReturn(patientId);
        final String motechIdForUpdatedPatient = allPatients.update(patient);
        assertThat(patientId, is(motechIdForUpdatedPatient));
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
        verify(mockOpenMRSRelationshipAdaptor).createMotherChildRelationship(motherId,childId);
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
        verify(mockOpenMRSRelationshipAdaptor).updateMotherRelationship(motherId, childId);
    }

    @Test
    public void shouldVoidMotherChildRelationship() {
        MRSPerson mockChild = mock(MRSPerson.class);
        String childId = "234";
        when(mockChild.getId()).thenReturn(childId);
        allPatients.voidMotherChildRelationship(mockChild);
        verify(mockOpenMRSRelationshipAdaptor).voidRelationship(childId);
    }

    @Test
    public void shouldGetMotherRelationship() {
        MRSPerson mockChild = mock(MRSPerson.class);
        String childId = "234";
        when(mockChild.getId()).thenReturn(childId);
        allPatients.getMotherRelationship(mockChild);
        verify(mockOpenMRSRelationshipAdaptor).getMotherRelationship(childId);
    }

    @Test
    public void shouldGetAgeOfAPerson() {
        String motechId = "1234567";

        when(mockMrsPatientAdaptor.getAgeOfPatientByMotechId(motechId)).thenReturn(3);
        allPatients.getAgeOfPersonByMotechId(motechId);

        verify(mockMrsPatientAdaptor).getAgeOfPatientByMotechId(motechId);
    }

    @Test
    public void shouldSaveCauseOfDeath() {
        String mrsPatientId = "patientId";
        Date dateOfDeath = DateUtil.now().toDate();
        allPatients.saveCauseOfDeath(dateOfDeath, mrsPatientId);
        verify(mockMrsPatientAdaptor).savePatientCauseOfDeathObservation(mrsPatientId, "NONE", dateOfDeath);
    }

}
