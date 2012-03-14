package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OldGhanaScheduleSource extends BaseSeedSource {

    public List<UpcomingSchedule> getUpcomingTTSchedules() {
        return querySchedulesFromDb("select patient_id, due_datetime, care_name from motechmodule_expected_obs where voided = 0 and group_name = 'TT' and care_name <> 'TT1' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingIPTSchedules() {
        return querySchedulesFromDb("select patient_id, due_date, care_name from motechmodule_expected_obs where voided = 0 and group_name = 'IPTI' order by patient_id");
    }

    private List<UpcomingSchedule> querySchedulesFromDb(String query) {
        return jdbcTemplate.query(query,
                new UpcomingScheduleRowMapper());
    }

}
