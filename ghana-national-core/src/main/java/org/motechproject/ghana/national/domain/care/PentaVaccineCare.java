package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.service.PentaDose;
import org.motechproject.ghana.national.vo.ChildCare;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_PENTA;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.util.DateUtil.newDateTime;

public class PentaVaccineCare extends ChildVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;


    public PentaVaccineCare(Patient patient,LocalDate enrollmentDate, Boolean hasActivePentaVaccine, String lastPentaDosage, Date lastPentaDate) {
        super(patient,hasActivePentaVaccine, ScheduleNames.CWC_PENTA, lastPentaDosage, lastPentaDate);
        this.enrollmentDate=enrollmentDate;
        this.patient = patient;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        ChildCare childCare = ChildCare.basedOnBirthDay(newDateTime(dateOfBirth));
        if (childCare.applicableForPenta())
            return new PatientCare(CWC_PENTA, childCare.birthDate(), enrollmentDate, null, patient.facilityMetaData());
        return null;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final PentaDose nextDosage = getNextOf(PentaDose.byValue(Integer.parseInt(dosage)));
        return nextDosage != null ? nextDosage.milestoneName() : null;
    }

}
