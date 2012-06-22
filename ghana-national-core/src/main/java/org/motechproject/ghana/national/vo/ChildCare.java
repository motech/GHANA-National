package org.motechproject.ghana.national.vo;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.util.DateUtil;

import static org.joda.time.PeriodType.weeks;
import static org.motechproject.ghana.national.domain.Constants.CWC_PENTA_MAX_WEEK_FOR_REGISTRATION;
import static org.motechproject.ghana.national.domain.Constants.CWC_PNEUMOCOCCAL_MAX_WEEK_FOR_REGISTRATION;
import static org.motechproject.ghana.national.domain.Constants.CWC_ROTAVIRUS_MAX_WEEK_FOR_REGISTRATION;
import static org.motechproject.util.DateUtil.today;

public class ChildCare {

    private DateTime birthTime;

    private ChildCare(DateTime birthTime) {
        this.birthTime = birthTime;
    }

    public static ChildCare basedOnBirthDay(DateTime birthTime) {
        return birthTime != null ? new ChildCare(birthTime) : null;
    }

    public int currentWeek() {
        return new Period(localDate().toDate().getTime(), today().toDate().getTime(), weeks()).getWeeks() + 1;
    }

    public boolean applicableForPenta() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= CWC_PENTA_MAX_WEEK_FOR_REGISTRATION;
    }

    public boolean applicableForRotavirus() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= CWC_ROTAVIRUS_MAX_WEEK_FOR_REGISTRATION;
    }

    public boolean applicableForPneumococcal() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= CWC_PNEUMOCOCCAL_MAX_WEEK_FOR_REGISTRATION;
    }
                                                 
    public boolean applicableForMeasles() {
        int runningYear = new Period(localDate(), today(), PeriodType.years()).getYears() + 1;
        return runningYear > 0 && runningYear <= Constants.CWC_MEASLES_MAX_AGE_WEEK_FOR_REGISTRATION;
    }

    private LocalDate localDate() {
        return DateUtil.newDate(birthTime.toDate());
    }

    public boolean applicableForIPTi() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= Constants.CWC_IPT_MAX_BIRTH_WEEK_FOR_REGISTRATION;
    }

    public DateTime birthTime() {
        return birthTime;
    }

    public LocalDate birthDate() {
        return DateUtil.newDate(birthTime.toDate());
    }

    public boolean applicableForOPV0() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= Constants.CWC_OPV0_MAX_BIRTH_WEEK_FOR_REGISTRATION;
    }

    public boolean applicableForOPVOther() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= Constants.CWC_OPV1_MAX_BIRTH_WEEK_FOR_REGISTRATION;
    }
}
