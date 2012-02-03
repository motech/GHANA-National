package org.motechproject.ghana.national.vo;

import org.joda.time.LocalDate;

public class Pregnancy {
    private LocalDate dateOfDelivery;

    private static final int NUMBER_OF_WEEKS_IN_PREGNANCY = 40;

    private Pregnancy(LocalDate dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    public static Pregnancy basedOnDeliveryDate(LocalDate dateOfDelivery) {
        return new Pregnancy(dateOfDelivery);
    }

    public LocalDate dateOfConception() {
        return dateOfDelivery.minusWeeks(NUMBER_OF_WEEKS_IN_PREGNANCY);
    }

    public LocalDate dateOfDelivery() {
        return dateOfDelivery;
    }

    public static Pregnancy basedOnConceptionDate(LocalDate conceptionDate) {
        return new Pregnancy(conceptionDate.plusWeeks(NUMBER_OF_WEEKS_IN_PREGNANCY));
    }
}
