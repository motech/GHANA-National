package org.motechproject.ghana.national.domain;

import java.util.List;

import static java.util.Arrays.asList;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;

public enum GeneralQueryType {
    OVERDUE_DELIVERIES(asList(ANC_DELIVERY)),
    RECENT_DELIVERIES(asList(ANC_DELIVERY)),
    UPCOMING_DELIVERIES(asList(ANC_DELIVERY)),
    ANC_DEFAULTERS(asList(ANC_IPT_VACCINE, TT_VACCINATION)),
    TT_DEFAULTERS(asList(TT_VACCINATION)),
    PNC_C_DEFAULTERS(asList(PNC_CHILD_1, PNC_CHILD_2, PNC_CHILD_3)),
    PNC_M_DEFAULTERS(asList(PNC_MOTHER_1, PNC_MOTHER_2, PNC_MOTHER_3)),
    CWC_DEFAULTERS(asList(CWC_IPT_VACCINE, CWC_OPV_0, CWC_OPV_OTHERS, CWC_PENTA));

    private List<String> scheduleNames;

    private GeneralQueryType(List<String> schedules) {
        this.scheduleNames = schedules;
    }

    public String[] getSchedules(){
        return (String[]) (this.scheduleNames).toArray();
    }
}
