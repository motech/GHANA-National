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
public class CommunitySource {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Community> getAllCommunities() {
        return jdbcTemplate.query("select id, community_id, name, facility_id, retired from motechmodule_community",
                new RowMapper<Community>() {
                    @Override
                    public Community mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        String id = resultSet.getString("id");
                        return new Community().id(id).communityId(resultSet.getString("community_id"))
                                .communityName(resultSet.getString("name")).facilityId(resultSet.getInt("facility_id"))
                                .retired(resultSet.getInt("retired")).patientIds(getAllPatienIdsForACommunity(id));
                    }
                });
    }

    public List<String> getAllPatienIdsForACommunity(String communityId) {
        return jdbcTemplate.query("select patient_id from motechmodule_community_patient where community_id=" + Integer.valueOf(communityId),
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return resultSet.getString("patient_id");
                    }
                });
    }



}
