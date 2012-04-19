package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class AllMotechModuleFacilities {
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(Facility facility) {
        if (facilityExists(facility.motechId())) {
            update(facility);
        } else {
            jdbcTemplate.update("insert into motechmodule_facility " +
                    "(facility_id,location_id,phone_number,additional_phone_number1,additional_phone_number2,additional_phone_number3) " +
                    "values (?,?,?,?,?,?)", facility.getMotechId(), facility.getMrsFacilityId(), facility.getPhoneNumber(),
                    facility.getAdditionalPhoneNumber1(), facility.getAdditionalPhoneNumber2(), facility.getAdditionalPhoneNumber3());
        }
    }

    boolean facilityExists(final String motechId) {
        return isNotEmpty(facility(motechId));
    }

    List<String> facility(final String motechId) {
        return jdbcTemplate.query(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement preparedStatement = connection.prepareStatement("SELECT facility_id FROM motechmodule_facility where facility_id=?");
                            preparedStatement.setString(1, motechId);
                            return preparedStatement;
                        }
                    },
                    new RowMapper<String>() {
                        @Override
                        public String mapRow(ResultSet resultSet, int i) throws SQLException {
                            return resultSet.getString("facility_id");
                        }
                    }
            );
    }

    public void update(Facility facility) {
        jdbcTemplate.update("update motechmodule_facility set " +
                "phone_number = ?,additional_phone_number1 = ?,additional_phone_number2 = ?,additional_phone_number3 = ? " +
                "where facility_id = ? ", facility.getPhoneNumber(),
                facility.getAdditionalPhoneNumber1(), facility.getAdditionalPhoneNumber2(), facility.getAdditionalPhoneNumber3(), facility.getMotechId());
    }
}
