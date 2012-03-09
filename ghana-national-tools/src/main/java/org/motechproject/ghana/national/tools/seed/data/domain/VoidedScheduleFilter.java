package org.motechproject.ghana.national.tools.seed.data.domain;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class VoidedScheduleFilter extends Filter{

    @Override
    public List<UpcomingSchedule> filteringLogic(List<UpcomingSchedule> schedules) {
        return ch.lambdaj.Lambda.filter(having(on(UpcomingSchedule.class).getVoided(), is(equalTo(false))), schedules);
    }
}
