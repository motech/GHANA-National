package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OldGhanaScheduleSource extends BaseSeedSource {

    public List<UpcomingSchedule> getUpcomingTTSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'TT' and care_name <> 'TT1' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingIPTSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'IPTI' and concept_id <> '984' order by patient_id");
    }

    private List<UpcomingSchedule> querySchedulesFromDb(String query) {
        return jdbcTemplate.query(query,
                new UpcomingScheduleRowMapper());
    }

}
