package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.IPTDose;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.Pregnancy;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;

public class IPTVaccineCare extends PregnancyVaccineCare {

    private Patient patient;

    public IPTVaccineCare(Patient patient, LocalDate expectedDeliveryDate, Boolean hasActiveIPTVaccine, String lastIPTDosage, Date lastIPTDate) {
        super(patient, expectedDeliveryDate, hasActiveIPTVaccine, ANC_IPT_VACCINE.getName(), lastIPTDosage, lastIPTDate);
        this.patient = patient;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate);
        return pregnancy.applicableForIPT() ?
                PatientCare.forEnrollmentFromStart(ANC_IPT_VACCINE.getName(), pregnancy.dateOfConception(), patient.facilityMetaData())
                : null;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final IPTDose nextDosage = getNextOf(IPTDose.byValue(dosage));
        return nextDosage != null ? nextDosage.milestone() : null;
    }

}
