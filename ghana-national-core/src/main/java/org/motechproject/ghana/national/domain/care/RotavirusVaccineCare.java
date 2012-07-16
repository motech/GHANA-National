package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.RotavirusDose;
import org.motechproject.ghana.national.vo.ChildCare;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_ROTAVIRUS;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.util.DateUtil.newDateTime;

public class RotavirusVaccineCare extends ChildVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;


    public RotavirusVaccineCare(Patient patient,LocalDate enrollmentDate, Boolean hasActiveRotavirusSchedule, String lastRotavirusDosage, Date lastRotavirusDate) {
        super(patient, hasActiveRotavirusSchedule, CWC_ROTAVIRUS.getName(), lastRotavirusDosage, lastRotavirusDate);
        this.enrollmentDate=enrollmentDate;
        this.patient = patient;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        ChildCare childCare = ChildCare.basedOnBirthDay(newDateTime(dateOfBirth));
        if (childCare.applicableForRotavirus())
            return new PatientCare(CWC_ROTAVIRUS.getName(), childCare.birthDate(), enrollmentDate, null, patient.facilityMetaData());
        return null;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final RotavirusDose nextDosage = getNextOf(RotavirusDose.byValue(Integer.parseInt(dosage)));
        return nextDosage != null ? nextDosage.milestoneName() : null;
    }


}
