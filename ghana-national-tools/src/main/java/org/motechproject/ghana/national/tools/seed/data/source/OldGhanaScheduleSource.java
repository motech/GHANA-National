package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OldGhanaScheduleSource extends BaseSeedSource {

    public List<UpcomingSchedule> getUpcomingTTSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name , min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'TT' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingIPTSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'IPTI' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingIPTPSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'IPT' order by patient_id");
    }

    public List<UpcomingSchedule> getEDDSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, encounter_type as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_encounter where voided = 0 and group_name = 'EDD' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingBCGSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'BCG' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingMeaslesSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'Measles' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingPentaSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'Penta' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingYellowFeverSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'YellowFever' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingPNCMotherSchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, encounter_type as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_encounter where voided = 0 and group_name = 'PNC(mother)' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingPNCBabySchedules() {
        return querySchedulesFromDb("select '' as motech_id, patient_id, encounter_type as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_encounter where voided = 0 and group_name = 'PNC(baby)' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingANCVisitSchedules() {
        return querySchedulesFromDb("SELECT pi.identifier as motech_id, mi.patient_id, mi.encounter_type as type, mi.care_name, mi.min_datetime, mi.due_datetime, mi.late_datetime, mi.max_datetime, mi.voided FROM (SELECT MAX(motechmodule_expected_encounter_id) AS mid FROM motechmodule_expected_encounter WHERE group_name = 'ANC' and voided = 0 GROUP BY patient_id) mo, motechmodule_expected_encounter mi, patient_identifier pi where mi.motechmodule_expected_encounter_id = mo.mid and pi.patient_id = mi.patient_id;");
    }

    public List<UpcomingSchedule> getUpcomingOPVSchedules() {
        return querySchedulesFromDb("(select '' as motech_id, d1.patient_id, d1.concept_id as type, d1.care_name , d1.min_datetime, d1.due_datetime, d1.late_datetime, d1.max_datetime, d1.voided from motechmodule_expected_obs d1 where d1.group_name = 'OPV' and d1.care_name = 'OPV0' and d1.voided = 0) union (select '' as motech_id, a1.patient_id, a1.concept_id as type, a1.care_name , a1.min_datetime, a1.due_datetime, a1.late_datetime, a1.max_datetime, a1.voided from motechmodule_expected_obs a1, (select b1.patient_id, max(b1.late_datetime) as late_datetime from motechmodule_expected_obs b1 where b1.group_name = 'OPV' and b1.care_name <> 'OPV0' and b1.voided = 0 group by b1.patient_id) c1 where a1.patient_id = c1.patient_id and a1.late_datetime = c1.late_datetime and a1.group_name = 'OPV' and a1.care_name <> 'OPV0' and a1.voided = 0)");
    }

    private List<UpcomingSchedule> querySchedulesFromDb(String query) {
        return jdbcTemplate.query(query,
                new UpcomingScheduleRowMapper());
    }
}
