package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.service.IPTiDose;
import org.motechproject.ghana.national.vo.ChildCare;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_IPT_VACCINE;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.util.DateUtil.newDateTime;

public class IPTiVaccineCare extends ChildVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;


    public IPTiVaccineCare(Patient patient, LocalDate enrollmentDate, Boolean hasActiveIPTiVaccine, String lastIPTiDosage, Date lastIPTiDate) {
        super(patient,hasActiveIPTiVaccine, ScheduleNames.CWC_IPT_VACCINE.getName(), lastIPTiDosage, lastIPTiDate);
        this.enrollmentDate=enrollmentDate;
        this.patient = patient;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        ChildCare childCare = ChildCare.basedOnBirthDay(newDateTime(dateOfBirth));
        if (childCare.applicableForIPTi())
            return new PatientCare(CWC_IPT_VACCINE.getName(), childCare.birthDate(), enrollmentDate, null, patient.facilityMetaData());
        return null;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final IPTiDose nextDosage = getNextOf(IPTiDose.byValue(Integer.parseInt(dosage)));
        return nextDosage != null ? nextDosage.milestoneName() : null;
    }

}
