package org.motechproject.ghana.national.tools.seed.data.source;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.LearnedFrom;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.ghana.national.domain.mobilemidwife.ReasonToJoin;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
public class MobileMidwifeSource {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<HashMap<String, String>> getEnrollmentData() {
        return jdbcTemplate.query("select program_name, person_id, start_date " +
                "from motechmodule_enrollment " +
                "where program_name != 'Expected Care Message Program'", new RowMapper<HashMap<String, String>>() {
            @Override
            public HashMap<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
                final HashMap<String, String> map = new HashMap<String, String>();
                map.put("patientId", resultSet.getString("person_id"));
                map.put("programName", resultSet.getString("program_name"));
                map.put("startDate", resultSet.getString("start_date"));
                return map;
            }
        });
    }

    public String getPatientMotechId(final String patientId) {
        List<String> list = jdbcTemplate.query(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement preparedStatement = connection.prepareStatement("select identifier from patient_identifier where patient_id = ?");
                        preparedStatement.setString(1, patientId);
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
        return (list.isEmpty()) ? null : list.get(0);
    }


    public List<MobileMidwifeEnrollment> getPatients() {
        return jdbcTemplate.query("select \n" +
                "pi.identifier as 'Identifier', \n" +
                "group_concat(if(pa.person_attribute_type_id = 8, value, null)) as 'Phone',\n" +
                "group_concat(if(pa.person_attribute_type_id = 11, value, null)) as 'Language',\n" +
                "group_concat(if(pa.person_attribute_type_id = 12, value, null)) as 'Phone_Type',\n" +
                "group_concat(if(pa.person_attribute_type_id = 13, value, null)) as 'Media',\n" +
                "group_concat(if(pa.person_attribute_type_id = 14, value, null)) as 'Day',\n" +
                "group_concat(if(pa.person_attribute_type_id = 15, value, null)) as 'Time',\n" +
                "group_concat(if(pa.person_attribute_type_id = 17, value, null)) as 'How',\n" +
                "group_concat(if(pa.person_attribute_type_id = 18, value, null)) as 'Reason'\n" +
                "from person_attribute pa, patient_identifier pi\n" +
                "where pa.person_id = pi.patient_id\n" +
                "and pa.person_attribute_type_id in (8,11,12,13,14,15,17,18)\n" +
                "and pi.identifier_type = 3\n" +
                "and pa.voided = 0\n" +
                "group by pa.person_id",

                new RowMapper<MobileMidwifeEnrollment>() {
                    @Override
                    public MobileMidwifeEnrollment mapRow(ResultSet resultSet, int i) throws SQLException {
                        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment();

                        enrollment.setPatientId(resultSet.getString(1));

                        enrollment.setPhoneNumber(resultSet.getString(2));

                        String phoneType = resultSet.getString(4);
                        if (StringUtils.isNotBlank(phoneType) && !",".equals(phoneType)) {
                            phoneType = correctText(phoneType);
                            enrollment.setPhoneOwnership(PhoneOwnership.valueOf(phoneType));
                        }

                        String language = resultSet.getString(3);
                        if (StringUtils.isNotBlank(language) && !",".equals(language)) {
                            language = correctText(language);
                            enrollment.setLanguage(Language.valueOf(language.toUpperCase()));
                        }

                        String media = resultSet.getString(5);
                        if (StringUtils.isNotBlank(media)) {
                            enrollment.setMedium(Medium.TEXT_SMS_MEDIUM.equals(media) ? Medium.SMS : Medium.VOICE);
                        }

                        String deliverDay = resultSet.getString(6);
                        if (StringUtils.isNotBlank(deliverDay) && !",".equals(deliverDay)) {
                            deliverDay = correctText(deliverDay);
                            enrollment.setDayOfWeek(DayOfWeek.valueOf(StringUtils.capitalize(deliverDay.toLowerCase())));
                        }

                        String time = resultSet.getString(7);
                        if (StringUtils.isNotBlank(time) && !",".equals(time)) {
                            time = correctText(time);
                            String[] times = time.split(":");
                            enrollment.setTimeOfDay(new Time(Integer.parseInt(times[0]), Integer.parseInt(times[1])));
                        }

                        String reason = resultSet.getString(9);
                        if (StringUtils.isNotBlank(reason) && !",".equals(reason)) {
                            reason = correctText(reason);
                            enrollment.setReasonToJoin(ReasonToJoin.valueOf(reason));
                        }

                        String how = resultSet.getString(8);
                        if (StringUtils.isNotBlank(how) && !",".equals(how)) {
                            how = correctText(how);
                            enrollment.setLearnedFrom(LearnedFrom.valueOf(how));
                        }
                        return enrollment;
                    }
                });
    }

    private String correctText(String how) {
        if (how.split(",").length == 2) {
            how = how.split(",")[0];
        }
        return how;
    }

    public static void main(String[] args) {
//        System.out.println(",".contains(","));
        System.out.println("select me.program_name as 'Program_Name', me.start_date as 'Start_Date', o.location_id, me.person_id, pi.identifier as 'MoTeCH_ID', u.system_id\n" +
                "                from motechmodule_enrollment me, obs o, patient_identifier pi, users u\n" +
                "                where o.obs_id = me.obs_id             \n" +
                "                and me.person_id = pi.patient_id\n" +
                "                and u.user_id = o.creator\n" +
                "                and pi.identifier_type = 3                \n" +
                "                and program_name in ('Weekly Pregnancy Message Program', 'Weekly Info Pregnancy Message Program')");
    }
}
