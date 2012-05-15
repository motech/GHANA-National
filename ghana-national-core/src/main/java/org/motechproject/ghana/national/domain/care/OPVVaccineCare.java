package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.OPVDose;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.ChildCare;

import java.util.Date;

import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_0;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_OTHERS;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.util.DateUtil.newDateTime;

public class OPVVaccineCare extends ChildVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;


    public OPVVaccineCare(Patient patient, LocalDate enrollmentDate, Boolean hasActiveOPVVaccine, String lastOPVDosage, Date lastOPVDate, String opvVaccine) {
        super(patient, hasActiveOPVVaccine, opvVaccine, lastOPVDosage, lastOPVDate);
        this.enrollmentDate = enrollmentDate;
        this.patient = patient;
    }

    @Override
    protected PatientCare newEnrollmentForCare() {
        ChildCare childCare = ChildCare.basedOnBirthDay(newDateTime(dateOfBirth));
        if (childCare.applicableForOPV0())
            return new PatientCare(CWC_OPV_0.getName(), childCare.birthDate(), enrollmentDate, null, patient.facilityMetaData());
        else if (childCare.applicableForOPVOther())
            return new PatientCare(CWC_OPV_OTHERS.getName(), childCare.birthDate(), enrollmentDate, null, patient.facilityMetaData());
        return null;
    }

    @Override
    protected String nextVaccineDose(String dosage) {
        final OPVDose nextDosage = getNextOf(OPVDose.byValue(dosage));
        return nextDosage != null ? nextDosage.milestoneName() : null;
    }
}
