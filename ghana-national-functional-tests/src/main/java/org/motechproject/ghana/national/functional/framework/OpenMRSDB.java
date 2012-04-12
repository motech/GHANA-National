package org.motechproject.ghana.national.functional.framework;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class OpenMRSDB {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public OpenMRSDB(@Qualifier("openMRSDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getOpenMRSId(final String motechId) {
        List<String> openMrsId = jdbcTemplate.query(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement preparedStatement = connection.prepareStatement("SELECT patient_id FROM patient_identifier where identifier=?");
                        preparedStatement.setString(1, motechId);
                        return preparedStatement;
                    }
                },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString("patient_id");
                    }
                }
        );
        return CollectionUtils.isEmpty(openMrsId) ? null : openMrsId.get(0);
    }

    public String getMotechIdByName(final String name) {
        List<String> motechId = jdbcTemplate.query(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement preparedStatement = connection.prepareStatement("select pi.identifier from patient_identifier pi, person_name pn where pn.person_id = pi.patient_id and pn.given_name = ?");
                        preparedStatement.setString(1, name);
                        return preparedStatement;
                    }
                },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString("identifier");
                    }
                }
        );
        return CollectionUtils.isEmpty(motechId) ? null : motechId.get(0);
    }
}
