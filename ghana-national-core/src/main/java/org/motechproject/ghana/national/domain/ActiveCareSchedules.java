package org.motechproject.ghana.national.domain;

import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.configuration.ScheduleNames.*;

public class ActiveCareSchedules {

    private Map<String, EnrollmentRecord> allActiveCareSchedules;

    public ActiveCareSchedules() {
        this.allActiveCareSchedules = new HashMap<String, EnrollmentRecord>();
    }

    public Boolean hasActiveTTSchedule() {
        return allActiveCareSchedules.get(TT_VACCINATION.getName()) != null;
    }

    public Boolean hasActiveIPTSchedule() {
        return allActiveCareSchedules.get(ANC_IPT_VACCINE.getName()) != null;
    }

    public Boolean hasActivePentaSchedule() {
        return allActiveCareSchedules.get(CWC_PENTA.getName()) != null;
    }

    public Boolean hasActiveIPTiSchedule() {
        return allActiveCareSchedules.get(CWC_IPT_VACCINE.getName()) != null;
    }

    public Boolean hasActiveOPVSchedule() {
        return allActiveCareSchedules.get(CWC_OPV_OTHERS.getName()) != null;
    }

    public ActiveCareSchedules setActiveCareSchedule(String careScheduleName, EnrollmentRecord enrollmentRecord) {
        allActiveCareSchedules.put(careScheduleName, enrollmentRecord);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveCareSchedules that = (ActiveCareSchedules) o;

        return !(allActiveCareSchedules != null ? !allActiveCareSchedules.equals(that.allActiveCareSchedules) : that.allActiveCareSchedules != null);

    }

    @Override
    public int hashCode() {
        return allActiveCareSchedules != null ? allActiveCareSchedules.hashCode() : 0;
    }
}
