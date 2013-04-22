package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.CWCVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;

@Repository
public class AllCWCVisitsForVisitor {
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(CWCVisit cwcVisit) {
        jdbcTemplate.update("insert into cwc_visitor (date_created, serial_number, immunizations, opv_dose, penta_dose," +
                " ipti_dose, rotavirus_dose, pneumococcal_dose, weight, muac, height, male_involved, cwc_location, house," +
                " community, comments, staff_id, facility_id, vitamin_a_dose, measles_dose, growth_monitoring_percentage, growth_monitoring_date)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Date(), cwcVisit.getSerialNumber(),
                cwcVisit.getImmunizations().toString(), cwcVisit.getOpvdose(), cwcVisit.getPentadose(), cwcVisit.getIptidose(),
                cwcVisit.getRotavirusdose(), cwcVisit.getPneumococcaldose(), cwcVisit.getWeight(), cwcVisit.getMuac(),
                cwcVisit.getHeight(), cwcVisit.getMaleInvolved(), cwcVisit.getCwcLocation(), cwcVisit.getHouse(),
                cwcVisit.getCommunity(), cwcVisit.getComments(), cwcVisit.getStaffId(), cwcVisit.getFacilityId(),
                cwcVisit.getVitaminadose(), cwcVisit.getMeaslesDose(), cwcVisit.getGrowthmonitoringpercentage(), cwcVisit.getGrowthmonitoringdate());
    }
}
