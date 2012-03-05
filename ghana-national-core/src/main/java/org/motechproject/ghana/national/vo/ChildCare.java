package org.motechproject.ghana.national.vo;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.util.DateUtil;

import static org.joda.time.PeriodType.weeks;
import static org.motechproject.ghana.national.domain.Constants.CWC_PENTA_MAX_WEEK_FOR_REGISTRATION;
import static org.motechproject.util.DateUtil.today;

public class ChildCare {

    private LocalDate birthDay;

    public ChildCare(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public static ChildCare basedOnBirthDay(LocalDate birthDay) {
        return new ChildCare(birthDay);
    }

    public int currentWeek() {
        return new Period(birthDay.toDate().getTime(), today().toDate().getTime(), weeks()).getWeeks() + 1;
    }

    public boolean applicableForPenta() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= CWC_PENTA_MAX_WEEK_FOR_REGISTRATION;
    }
                                                 
    public boolean applicableForMeasles() {
        int runningYear = new Period(DateUtil.newDate(birthDay.toDate()), today(), PeriodType.years()).getYears() + 1;
        return runningYear > 0 && runningYear <= Constants.CWC_MEASLES_MAX_AGE_WEEK_FOR_REGISTRATION;
    }

    public boolean applicableForIPT() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= Constants.CWC_IPT_MAX_BIRTH_WEEK_FOR_REGISTRATION;
    }
}
