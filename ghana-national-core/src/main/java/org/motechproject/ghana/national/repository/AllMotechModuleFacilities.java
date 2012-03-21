package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AllMotechModuleFacilities {
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(Facility facility) {
        jdbcTemplate.update("insert into motechmodule_facility " +
                "(facility_id,location_id,phone_number,additional_phone_number1,additional_phone_number2,additional_phone_number3) " +
                "values (?,?,?,?,?,?)", facility.getMotechId(), facility.getMrsFacilityId(), facility.getPhoneNumber(),
                facility.getAdditionalPhoneNumber1(), facility.getAdditionalPhoneNumber2(), facility.getAdditionalPhoneNumber3());
    }

    public void update(Facility facility) {
        jdbcTemplate.update("update motechmodule_facility set " +
                "phone_number = ?,additional_phone_number1 = ?,additional_phone_number2 = ?,additional_phone_number3 = ? " +
                "where facility_id = ? ", facility.getPhoneNumber(),
                facility.getAdditionalPhoneNumber1(), facility.getAdditionalPhoneNumber2(), facility.getAdditionalPhoneNumber3(), facility.getMotechId());
    }
}
