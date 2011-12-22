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
public class CommunitySource {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Community> getAllCommunities() {

        return jdbcTemplate.query("select com.id id, com.community_id community_id, com.name name, com.facility_id facility_id, com.retired retired, l.name location_name from motechmodule_community com, location l",
                new RowMapper<Community>() {
                    @Override
                    public Community mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return new Community().id(resultSet.getString("id")).communityId(resultSet.getString("community_id"))
                                .communityName(resultSet.getString("name")).facilityId(resultSet.getInt("facility_id"))
                                .retired(resultSet.getInt("retired"))
                                .facilityName(resultSet.getString("location_name"));
                    }
                });
    }

    public Map<String, String> patientsAssociatedToCommunity() {

        SqlRowSet query = jdbcTemplate.queryForRowSet("select patient_id, community_id from motechmodule_community_patient");
        Map<String, String> result = new HashMap<String, String>();
        while (query.next()) {
            result.put(query.getString("patient_id"), query.getString("community_id"));
        }
        return result;
    }


}
