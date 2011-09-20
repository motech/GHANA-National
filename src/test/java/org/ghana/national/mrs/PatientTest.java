package org.ghana.national.mrs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.openmrs.api.context.Context.*;

public class PatientTest {
    @Before
    public void setUp() throws InputRequiredException, DatabaseUpdateException, IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("openmrs.properties");
        properties.load(in);
        in.close();

        String property = properties.getProperty("connection.string");

        startup("jdbc:mysql://10.15.6.34:3306/openmrs?autoReconnect=true", "openmrs", "password", new Properties());
        openSession();
        authenticate("admin", "OhVeQp7r");
    }

    @After
    public void tearDown() {
        closeSession();
    }

    @Test
    public void shouldLoadAllPatients() {
        List<Patient> patients = getPatientService().getAllPatients();
        for (Patient patient : patients) {
            System.out.println("Patient : " + patient.getPersonName());
        }
    }
}
