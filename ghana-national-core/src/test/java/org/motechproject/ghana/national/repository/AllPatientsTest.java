package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class AllPatientsTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    AllPatients allPatients;

    @Qualifier("couchDbConnector")
    @Autowired
    protected CouchDbConnector dbConnector;

    @Mock
    MRSPatientAdaptor mockMrsPatientAdaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ReflectionTestUtils.setField(allPatients, "patientAdaptor", mockMrsPatientAdaptor);
    }

    @Test
    public void shouldSaveAPatient() {
        final MRSFacility facility = new MRSFacility("1", "facility", "country", "region", "district", "state");
        final String first = "first";
        final String middle = "middle";
        final String last = "last";
        final String preferred = "preferred";
        final Date dateOfBirth = new Date();
        final String gender = "male";
        final String address = "address";
        final String patientId = "1000";
        Boolean birthDateEstimated = true;
        final MRSPatient mrsPatient = new MRSPatient(first, middle, last, preferred, dateOfBirth, birthDateEstimated, gender, address, facility);
        final Patient patient = new Patient(mrsPatient);
        MRSPatient savedPatient = mock(MRSPatient.class);
        when(savedPatient.getId()).thenReturn(patientId);
        when(mockMrsPatientAdaptor.savePatient(mrsPatient)).thenReturn(savedPatient);

        final int initialSize = allPatients.getAll().size();

        allPatients.add(patient);

        final List<Patient> patients = allPatients.getAll();
        final int actualSize = patients.size();
        assertThat(actualSize, is(equalTo(initialSize + 1)));
        final Patient actualPatient = patients.iterator().next();

        assertThat(actualPatient.mrsPatientId(), is(equalTo(patientId)));
    }

    @Test
    public void shouldReturnNullIfFetchPatientByIdIsNull() {
        final String patientId = "1";
        when(mockMrsPatientAdaptor.getPatientByMotechId(patientId)).thenReturn(null);
        final Patient actualPatient = allPatients.patientById(patientId);
        assertNull(actualPatient);
    }

    @Test
    public void shouldFetchPatientById() {
        final String patientId = "1";
        MRSPatient patient = new MRSPatient(null, null, null, null, null, null, null, null, null);
        when(mockMrsPatientAdaptor.getPatientByMotechId(patientId)).thenReturn(patient);
        final Patient actualPatient = allPatients.patientById(patientId);
        assertThat(actualPatient.mrsPatient(), is(patient));
    }

    @After
    public void tearDown() throws Exception {
        List<Patient> all = allPatients.getAll();
        for (Patient patient : all)
            allPatients.remove(patient);
    }
}
