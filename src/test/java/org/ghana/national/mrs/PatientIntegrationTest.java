package org.ghana.national.mrs;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.openmrs.api.context.Context.closeSession;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/applicationContext.xml", "classpath*:META-INF/spring/applicationContext-openmrs.xml"})
public class PatientIntegrationTest {
    @Autowired
    private PatientService patientService;

    @After
    public void tearDown() {
        closeSession();
    }

    @Test
    public void shouldLoadAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        for (Patient patient : patients) {
            System.out.println("Patient : " + patient.getPersonName());
        }
    }
}
