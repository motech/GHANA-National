package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSchedule {

    @Autowired
    AllSchedules allSchedules;

    public LocalDate referenceDateToStartInDueWindow(String scheduleName) {
        Period startOfDue = windowPeriod(scheduleName, WindowName.due);
        return dateBackInPast(startOfDue).toLocalDate();
    }

    public LocalDate referenceDateToStartInLateWindow(String scheduleName) {
        Period startOfLate = windowPeriod(scheduleName, WindowName.late);
        return dateBackInPast(startOfLate).toLocalDate();
    }

    public LocalDate referenceDateToStartInLateWindow(String scheduleName, Period periodToAddForWindow) {
        Period startOfLate = windowPeriod(scheduleName, WindowName.late);
        return dateBackInPast(startOfLate).minus(periodToAddForWindow).toLocalDate();
    }

    private Period windowPeriod(String scheduleName, WindowName window) {
        Schedule schedule = allSchedules.getByName(scheduleName);
        return schedule.getFirstMilestone().getWindowStart(window);
    }

    private DateTime dateBackInPast(Period period) {
        return DateUtil.now().minus(period);
    }
}
