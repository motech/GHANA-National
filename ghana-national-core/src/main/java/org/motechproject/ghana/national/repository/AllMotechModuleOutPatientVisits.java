package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AllMotechModuleOutPatientVisits {
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(OutPatientVisit outPatientVisit) {
        jdbcTemplate.update("insert into motechmodule_generaloutpatientencounter (visit_date, staff_id, facility_id, serial_number," +
                " sex,birthdate,insured,newcase,newpatient,diagnosis,secondary_diagnosis,referred,rdt_given,rdt_positive," +
                "act_treated,comments) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", outPatientVisit.getVisitDate(), outPatientVisit.getStaffId(),
                outPatientVisit.getFacilityId(), outPatientVisit.getSerialNumber(), outPatientVisit.getGender(), outPatientVisit.getDateOfBirth(),
                outPatientVisit.getInsured(), outPatientVisit.getNewCase(), outPatientVisit.getNewPatient(), outPatientVisit.getDiagnosis(),
                outPatientVisit.getSecondDiagnosis(), outPatientVisit.getReferred(), outPatientVisit.getRdtGiven(), outPatientVisit.getRdtPositive(),
                outPatientVisit.getActTreated(), outPatientVisit.getComments());
    }
}
