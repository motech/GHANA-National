package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.PatientSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

import static java.lang.String.format;

@Component
public class PatientSeed extends Seed {

    private JdbcTemplate jdbcTemplate;
    private PatientSource patientSource;

    @Autowired
    public void init(DataSource dataSource, PatientSource patientSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.patientSource = patientSource;
    }
    
    @Override
    protected void load() {
        updatePatientsWithLocationId();
    }

    private void updatePatientsWithLocationId() {
        Map<String, String> allPatientsWithFacilities = patientSource.getAllPatientsWithFacilities();
        String updatePatientLocationSql = "update patient_identifier set location_id=%s where patient_id=%s";
        for(String patientId: allPatientsWithFacilities.keySet()){
            int update = jdbcTemplate.update(format(updatePatientLocationSql, allPatientsWithFacilities.get(patientId), patientId));
            assert 1 == update;
        }
    }
}
