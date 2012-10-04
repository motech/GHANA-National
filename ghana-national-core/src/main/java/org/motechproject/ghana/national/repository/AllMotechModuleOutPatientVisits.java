package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AllMotechModuleOutPatientVisits {
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(OutPatientVisit outPatientVisit) {
        jdbcTemplate.update("insert into motechmodule_generaloutpatientencounter (visit_date, staff_id, facility_id, serial_number," +
                " sex,birthdate,insured,newcase,newpatient,diagnosis,secondary_diagnosis,referred,rdt_given,rdt_positive," +
                "act_treated,comments) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", outPatientVisit.getVisitDate(), outPatientVisit.getStaffId(),
                outPatientVisit.getFacilityId(), outPatientVisit.getSerialNumber(), changeGenderFormat(outPatientVisit.getGender()), outPatientVisit.getDateOfBirth(),
                booleanToInt(outPatientVisit.getInsured()), booleanToInt(outPatientVisit.getNewCase()), booleanToInt(outPatientVisit.getNewPatient()),
                outPatientVisit.getDiagnosis(), outPatientVisit.getSecondDiagnosis(), booleanToInt(outPatientVisit.getReferred()),
                booleanToInt(outPatientVisit.getRdtGiven()), booleanToInt(outPatientVisit.getRdtPositive()),
                booleanToInt(outPatientVisit.getActTreated()), outPatientVisit.getComments());
    }

    private String changeGenderFormat(String couchValue) {
        return (couchValue == null) ? null : (couchValue.equals("M") ? "MALE" : "FEMALE");
    }

    private Integer booleanToInt(Boolean couchValue) {
        return (couchValue != null) ? (couchValue ? 1 : 0) : null;
    }

    public boolean isDuplicate(OutPatientVisit outPatientVisit) {
        List<String> results = jdbcTemplate.query("SELECT id " +
                "FROM motechmodule_generaloutpatientencounter " +
                "WHERE serial_number = ? " +
                "AND facility_id = ? " +
                "AND birthdate = ? " +
                "AND diagnosis = ?",
                new Object[]{outPatientVisit.getSerialNumber(), outPatientVisit.getFacilityId(),
                        outPatientVisit.getDateOfBirth(), outPatientVisit.getDiagnosis()}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        });

        return !results.isEmpty();
    }
}
