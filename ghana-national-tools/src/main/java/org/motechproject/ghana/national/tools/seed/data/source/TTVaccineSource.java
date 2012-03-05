package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TTVaccineSource extends BaseSeedSource {

    public List<UpcomingSchedule> getAllUpcomingSchedules() {

        return jdbcTemplate.query("select patient_id, due_datetime, care_name from motechmodule_expected_obs where voided = 0 and group_name = 'TT' order by patient_id",
                new RowMapper<UpcomingSchedule>() {
                    @Override
                    public UpcomingSchedule mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return new UpcomingSchedule(resultSet.getString("patient_id"), resultSet.getString("due_datetime"), resultSet.getString("care_name"));
                    }
                });
    }

}
