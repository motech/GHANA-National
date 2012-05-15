package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.TTVaccineDosage;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;

public class TTVaccineCare extends PregnancyVaccineCare {
    private LocalDate enrollmentDate;

    public TTVaccineCare(Patient patient, LocalDate expectedDateOfDelivery, LocalDate enrollmentDate, Boolean hasActiveTTSchedule, String lastTTDosage, Date lastTTDate) {
        super(patient, expectedDateOfDelivery, hasActiveTTSchedule, TT_VACCINATION.getName(), lastTTDosage, lastTTDate);
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final TTVaccineDosage nextDosage = getNextOf(TTVaccineDosage.byValue(Integer.valueOf(dosage)));
        return nextDosage != null ? nextDosage.getScheduleMilestoneName() : null;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        return PatientCare.forEnrollmentFromStart(TT_VACCINATION.getName(), enrollmentDate, patient.facilityMetaData());
    }
}
