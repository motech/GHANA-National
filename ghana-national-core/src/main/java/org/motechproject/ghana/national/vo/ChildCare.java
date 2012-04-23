package org.motechproject.ghana.national.vo;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import static org.joda.time.PeriodType.weeks;
import static org.motechproject.ghana.national.domain.Constants.PENTA_MAX_PREGNANCY_WEEK_FOR_REGISTRATION;
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
        return currentWeek > 0 && currentWeek <= PENTA_MAX_PREGNANCY_WEEK_FOR_REGISTRATION;
    }
}
