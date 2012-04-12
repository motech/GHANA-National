package org.motechproject.ghana.national.domain;

import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;

public class ActiveCareSchedules {

    private Map<String, EnrollmentRecord> allActiveCareSchedules;

    public ActiveCareSchedules() {
        this.allActiveCareSchedules = new HashMap<String, EnrollmentRecord>();
    }

    public Boolean hasActiveTTSchedule() {
        return allActiveCareSchedules.get(TT_VACCINATION) != null;
    }

    public Boolean hasActiveIPTSchedule() {
        return allActiveCareSchedules.get(ANC_IPT_VACCINE) != null;
    }

    public ActiveCareSchedules setActiveCareSchedule(String careScheduleName, EnrollmentRecord enrollmentRecord){
        allActiveCareSchedules.put(careScheduleName, enrollmentRecord);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveCareSchedules that = (ActiveCareSchedules) o;

        if (allActiveCareSchedules != null ? !allActiveCareSchedules.equals(that.allActiveCareSchedules) : that.allActiveCareSchedules != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return allActiveCareSchedules != null ? allActiveCareSchedules.hashCode() : 0;
    }
}
