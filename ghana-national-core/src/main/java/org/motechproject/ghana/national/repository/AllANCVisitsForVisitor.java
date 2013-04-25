package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.ANCVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;

@Repository
public class AllANCVisitsForVisitor {
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(ANCVisit ancVisit) {
        jdbcTemplate.update("insert into anc_visitor(serial_number, date_created, visit_number, est_delivery_date, " +
                "bp_systolic, bp_diastolic, weight, tt_dose, ipt_dose, ipt_reactive, itn_use, fht, fhr, " +
                "urine_test_protein_positive, urine_test_glucose_positive, hemoglobin, vdrl_reactive, vdrl_treatment, " +
                "dewormer, pmtct, pre_test_counseled, hiv_test_result, post_test_counseled, pmtct_treatment, location, " +
                "house, community, referred, male_involved, next_anc_date, comments, staff_id, facility_id) values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                ancVisit.getSerialNumber(), new Date(), ancVisit.getVisitNumber(), ancVisit.getEstDeliveryDate(),
                ancVisit.getBpSystolic(), ancVisit.getBpDiastolic(), ancVisit.getWeight(), ancVisit.getTtdose(),
                ancVisit.getIptdose(), ancVisit.getIptReactive(), ancVisit.getItnUse(), ancVisit.getFht(), ancVisit.getFhr(),
                ancVisit.getUrineTestProteinPositive(), ancVisit.getUrineTestGlucosePositive(), ancVisit.getHemoglobin(),
                ancVisit.getVdrlReactive(), ancVisit.getVdrlTreatment(), ancVisit.getDewormer(), ancVisit.getPmtct(),
                ancVisit.getPreTestCounseled(), ancVisit.getHivTestResult(), ancVisit.getPostTestCounseled(),
                ancVisit.getPmtctTreament(), ancVisit.getLocation(), ancVisit.getHouse(), ancVisit.getCommunity(),
                ancVisit.getReferred(), ancVisit.getMaleInvolved(), ancVisit.getNextANCDate(), ancVisit.getComments(),
                ancVisit.getStaffId(), ancVisit.getFacilityId());
    }
}

