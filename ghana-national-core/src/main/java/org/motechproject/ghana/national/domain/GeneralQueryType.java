package org.motechproject.ghana.national.domain;

import org.motechproject.ghana.national.configuration.ScheduleNames;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_DELIVERY;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_0;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_OTHERS;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_PENTA;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_CHILD_1;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_CHILD_2;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_CHILD_3;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_1;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_2;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_3;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;

public enum GeneralQueryType {
    OVERDUE_DELIVERIES("Overdue Defaulters",asList(ANC_DELIVERY)),
    RECENT_DELIVERIES("Recent Deliveries",asList(ANC_DELIVERY)),
    UPCOMING_DELIVERIES("Upcoming Deliveries",asList(ANC_DELIVERY)),
    ANC_DEFAULTERS("ANC Defaulters",asList(ANC_IPT_VACCINE, TT_VACCINATION)),
    TT_DEFAULTERS("TT Defaulters",asList(TT_VACCINATION)),
    PNC_C_DEFAULTERS("PNC Child Defaulters",asList(PNC_CHILD_1, PNC_CHILD_2, PNC_CHILD_3)),
    PNC_M_DEFAULTERS("PNC Mother Defaulters",asList(PNC_MOTHER_1, PNC_MOTHER_2, PNC_MOTHER_3)),
    CWC_DEFAULTERS("CWC Defaulters",asList(CWC_IPT_VACCINE, CWC_OPV_0, CWC_OPV_OTHERS, CWC_PENTA));

    private String name;
    private List<ScheduleNames> scheduleNames;

    private GeneralQueryType(String name,List<ScheduleNames> schedules) {
        this.name=name;
        this.scheduleNames = schedules;
    }

    public String[] getSchedules(){
        List<String> names=new ArrayList<String>();
        for (ScheduleNames scheduleName : scheduleNames) {
            names.add(scheduleName.getName());
        }
        return names.toArray(new String[names.size()]);
    }

    public String[] getFriendlyNames(){
        List<String> names=new ArrayList<String>();
        for (ScheduleNames scheduleName : scheduleNames) {
            names.add(scheduleName.getFriendlyName());
        }
        return names.toArray(new String[names.size()]);

    }

    public String getName(){
        return this.name;
    }
}
