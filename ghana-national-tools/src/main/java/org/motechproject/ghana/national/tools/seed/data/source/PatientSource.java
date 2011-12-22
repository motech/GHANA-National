package org.motechproject.ghana.national.tools.seed.data.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PatientSource {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Map<String,String> getAllPatientsWithFacilities() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select fac.location_id location_id, facPatient.patient_id  patient_id from motechmodule_facility_patient facPatient, motechmodule_facility fac where fac.id=facPatient.facility_id");
        Map<String, String> patientsWithFacilities = new HashMap<String, String>();
        while (sqlRowSet.next()) {
            patientsWithFacilities.put(sqlRowSet.getString("patient_id"), sqlRowSet.getString("location_id"));
        }
        return patientsWithFacilities;
    }
}
