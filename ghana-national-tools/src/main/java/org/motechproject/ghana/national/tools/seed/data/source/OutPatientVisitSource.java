package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.domain.PatientType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class OutPatientVisitSource extends BaseSeedSource {

    public List<OutPatientVisit> getOutPatientVisitList() {
        return jdbcTemplate.query("select * from motechmodule_generaloutpatientencounter",
                new RowMapper<OutPatientVisit>() {
                    @Override
                    public OutPatientVisit mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return new OutPatientVisit().setVisitDate(resultSet.getDate("visit_date"))
                                .setStaffId(resultSet.getString("staff_id"))
                                .setFacilityId(resultSet.getString("facility_id"))
                                .setSerialNumber(resultSet.getString("serial_number"))
                                .setGender(resultSet.getString("sex"))
                                .setDateOfBirth(resultSet.getDate("birthdate"))
                                .setInsured(resultSet.getBoolean("insured"))
                                .setNewCase(resultSet.getBoolean("newcase"))
                                .setNewPatient(resultSet.getBoolean("newpatient"))
                                .setDiagnosis(resultSet.getInt("diagnosis"))
                                .setSecondDiagnosis(resultSet.getInt("secondary_diagnosis"))
                                .setReferred(resultSet.getBoolean("referred"))
                                .setRdtGiven(resultSet.getBoolean("rdt_given"))
                                .setRdtPositive(resultSet.getBoolean("rdt_positive"))
                                .setActTreated(resultSet.getBoolean("act_treated"))
                                .setComments(resultSet.getString("comments"))
                                .setRegistrantType(PatientType.OTHER);
                    }
                });
    }
}
