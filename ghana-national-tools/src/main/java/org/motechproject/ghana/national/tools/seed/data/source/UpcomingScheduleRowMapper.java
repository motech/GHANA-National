package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpcomingScheduleRowMapper implements RowMapper<UpcomingSchedule>{
    @Override
    public UpcomingSchedule mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new UpcomingSchedule(resultSet.getString("patient_id"), resultSet.getString("due_datetime"),
                resultSet.getString("care_name"), resultSet.getString("type"),
                resultSet.getString("min_datetime"), resultSet.getString("late_datetime"),
                resultSet.getString("max_datetime"), resultSet.getInt("voided"), resultSet.getString("motech_id"));
    }
}
