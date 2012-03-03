package org.motechproject.ghana.national.vo;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import static org.joda.time.PeriodType.weeks;
import static org.motechproject.ghana.national.domain.Constants.IPT_MAX_PREGNANCY_WEEK_FOR_REGISTRATION;
import static org.motechproject.util.DateUtil.today;

public class Pregnancy {
    private LocalDate dateOfDelivery;

    private static final int NUMBER_OF_WEEKS_IN_PREGNANCY = 40;

    private Pregnancy(LocalDate dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    public static Pregnancy basedOnConceptionDate(LocalDate conceptionDate) {
        return new Pregnancy(conceptionDate.plusWeeks(NUMBER_OF_WEEKS_IN_PREGNANCY));
    }

    public static Pregnancy basedOnDeliveryDate(LocalDate dateOfDelivery) {
        return new Pregnancy(dateOfDelivery);
    }

    public LocalDate dateOfConception() {
        return dateOfConception(dateOfDelivery);
    }

    private LocalDate dateOfConception(LocalDate dateOfDelivery) {
        return dateOfDelivery.minusWeeks(NUMBER_OF_WEEKS_IN_PREGNANCY);
    }

    public LocalDate dateOfDelivery() {
        return dateOfDelivery;
    }

    public int currentWeek() {
        LocalDate today = today();
        LocalDate conceptionDate = dateOfConception(dateOfDelivery);
        return new Period(conceptionDate.toDate().getTime(), today.toDate().getTime(), weeks()).getWeeks() + 1;
    }

    public boolean applicableForIPT() {
        int currentWeek = currentWeek();
        return currentWeek > 0 && currentWeek <= IPT_MAX_PREGNANCY_WEEK_FOR_REGISTRATION;
    }
}
