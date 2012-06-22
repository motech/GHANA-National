package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.PneumococcalDose;
import org.motechproject.ghana.national.vo.ChildCare;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_PNEUMOCOCCAL;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.util.DateUtil.newDateTime;

public class PneumococcalVaccineCare extends ChildVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;


    public PneumococcalVaccineCare(Patient patient, LocalDate enrollmentDate, Boolean hasActivePneumococcalSchedule, String lastPneumococcalDosage, Date lastPneumococcalDate) {
        super(patient, hasActivePneumococcalSchedule, CWC_PNEUMOCOCCAL.getName(), lastPneumococcalDosage, lastPneumococcalDate);
        this.enrollmentDate=enrollmentDate;
        this.patient = patient;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        ChildCare childCare = ChildCare.basedOnBirthDay(newDateTime(dateOfBirth));
        if (childCare.applicableForPneumococcal())
            return new PatientCare(CWC_PNEUMOCOCCAL.getName(), childCare.birthDate(), enrollmentDate, null, patient.facilityMetaData());
        return null;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final PneumococcalDose nextDosage = getNextOf(PneumococcalDose.byValue(Integer.parseInt(dosage)));
        return nextDosage != null ? nextDosage.milestoneName() : null;
    }


}
