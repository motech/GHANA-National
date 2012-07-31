package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.OldGhanaFacility;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FacilitySource extends BaseSeedSource {
    public List<OldGhanaFacility> getMotechFacilityNameAndIds() {
        return jdbcTemplate.query("select loc.name, fac.facility_id, fac.phone_number, fac.additional_phone_number1, fac.additional_phone_number2, fac.additional_phone_number3 from motechmodule_facility fac, location loc where loc.location_id = fac.location_id",
                new RowMapper<OldGhanaFacility>() {
                    @Override
                    public OldGhanaFacility mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return new OldGhanaFacility(resultSet.getString("name"),
                                String.valueOf(resultSet.getInt("facility_id")),
                                String.valueOf(resultSet.getString("phone_number")),
                                String.valueOf(resultSet.getString("additional_phone_number1")),
                                String.valueOf(resultSet.getString("additional_phone_number2")),
                                String.valueOf(resultSet.getString("additional_phone_number3")));
                    }
                });
    }

}
