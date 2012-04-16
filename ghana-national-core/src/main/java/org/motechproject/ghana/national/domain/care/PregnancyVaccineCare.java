package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.Pregnancy;

import java.util.Date;

public abstract class PregnancyVaccineCare extends VaccineCare {
    protected LocalDate expectedDeliveryDate;

    public PregnancyVaccineCare(Patient patient, LocalDate expectedDeliveryDate, Boolean hasActiveSchedule, String vaccineName, String lastDosage, Date lastTakenDate) {
        super(patient, hasActiveSchedule, vaccineName, lastDosage, lastTakenDate);
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    @Override
    public boolean isWithinActiveRange() {
        return isWithinCurrentPregnancyPeriod();
    }

    private boolean isWithinCurrentPregnancyPeriod() {
        if (expectedDeliveryDate != null) {
            LocalDate dateOfConception = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate).dateOfConception();
            LocalDate eddMaxDate = expectedDeliveryDate.plusWeeks(4);
            return lastTakenDate != null && lastTakenDate.after(dateOfConception.toDate()) && lastTakenDate.before(eddMaxDate.toDate());
        }
        return false;
    }
}
