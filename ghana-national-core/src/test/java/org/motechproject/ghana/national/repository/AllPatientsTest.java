package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

@Ignore("Work in Progress.")
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
        ReflectionTestUtils.setField(allPatients, "facilityAdaptor", mockMrsPatientAdaptor);
    }

    @After
    public void tearDown() throws Exception {
        List<Patient> all = allPatients.getAll();
        for (Patient patient : all)
            allPatients.remove(patient);
    }
}
