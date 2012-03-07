package org.motechproject.ghana.national.repository;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.util.DateUtil;
import org.motechproject.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class SearchMRSPatient {

    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void SearchMRSPatient(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @LoginAsAdmin
    @ApiSession
    public List<MRSPatient> getPatients(final String firstName, String lastName, String phoneNumber, final Date dateOfBirth, String nhis) {
        String query = createQuery(firstName, lastName, phoneNumber, dateOfBirth, nhis);
        return jdbcTemplate.query(query, new RowMapper<MRSPatient>() {
            @Override
            public MRSPatient mapRow(ResultSet rs, int rowNum) throws SQLException {
                MRSFacility facility = new MRSFacility(rs.getString("facility"), null, null, null, null);
                MRSPerson person = new MRSPerson().firstName(rs.getString("firstName")).lastName(rs.getString("lastName")).gender(rs.getString("gender")).dateOfBirth(dateOfBirth);
                return new MRSPatient(rs.getString("motechId"), person, facility);
            }
        });

    }

    String createQuery(String firstName, String lastName, String phoneNumber, Date dateOfBirth, String nhis) {
        String dateString = (dateOfBirth != null) ? new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.newDateTime(dateOfBirth).toDate()) : "";   //if date is null

        String select = "SELECT " +

                "pname.given_name as firstName, " +
                "pname.family_name as lastName, " +
                "per.gender as gender, " +
                "per.birthdate as dateOfBirth, " +
                "p_identifier.identifier as motechId, " +
                "loc.name as facility";

        String from = "FROM " +

                "person_name pname, " +
                "patient pat, person per, " +
                "patient_identifier p_identifier, " +
                "location loc";


        String where = "WHERE " +
                "p_identifier.patient_id = pat.patient_id AND " +
                "p_identifier.location_id = loc.location_id AND " +
                "pat.voided = 0 AND " +
                "p_identifier.identifier_type = 3 AND " +
                "pat.patient_id=per.person_id AND " +
                "per.person_id=pname.person_id";

        List<String> fromList = new ArrayList<String>();
        fromList.add(from);

        List<String> whereList = new ArrayList<String>();
        whereList.add(where);

        if (!StringUtil.isNullOrEmpty(firstName)) {
            whereList.add("pname.given_name LIKE '" + firstName + "%'");
        }
        if (!StringUtil.isNullOrEmpty(lastName)) {
            whereList.add("pname.family_name LIKE '" + lastName + "%'");  //like %
        }
        if (dateOfBirth != null) {
            whereList.add("per.birthdate = '" + dateString + "'");
        }
        if (!StringUtil.isNullOrEmpty(phoneNumber)) {
            fromList.add("person_attribute phone");
            whereList.add("phone.person_id = per.person_id AND " +
                    "phone.person_attribute_type_id=8 AND " +
                    "phone.value='" + phoneNumber + "'");
        }
        if (!StringUtil.isNullOrEmpty(nhis)) {
            fromList.add("person_attribute nhis");
            whereList.add("nhis.person_id=per.person_id AND " +
                    "nhis.person_attribute_type_id=9 AND " +
                    "nhis.value='" + nhis + "'");
        }

        String whereConjunction = " AND ";
        String fromConjunction = ", ";

        from = StringUtils.join(fromList, fromConjunction);
        where = StringUtils.join(whereList, whereConjunction);

        String query = select + " " + from + " " + where + " limit 20";
        return query;
    }
}
