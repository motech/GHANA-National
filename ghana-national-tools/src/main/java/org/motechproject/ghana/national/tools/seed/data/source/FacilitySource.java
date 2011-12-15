package org.motechproject.ghana.national.tools.seed.data.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FacilitySource {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<FacilityNameAndId> getMotechFacilityNameAndIds() {
        return jdbcTemplate.query("select loc.name, fac.facility_id from motechmodule_facility fac, location loc where loc.location_id = fac.location_id",
                new RowMapper<FacilityNameAndId>() {
                    @Override
                    public FacilityNameAndId mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return new FacilityNameAndId(resultSet.getString("name"), String.valueOf(resultSet.getInt("facility_id")));
                    }
                });
    }

}
