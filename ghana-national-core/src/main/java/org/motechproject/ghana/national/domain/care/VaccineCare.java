package org.motechproject.ghana.national.domain.care;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;

import java.util.Date;

import static org.motechproject.util.DateUtil.newDate;

public abstract class VaccineCare {
    protected Patient patient;
    private Boolean hasActiveSchedule;
    private String vaccineName;
    private String lastDosage;
    private Date lastTakenDate;
    private Boolean isPastVaccinationDateWithinRange;

    public VaccineCare(Patient patient, Boolean hasActiveSchedule, String vaccineName, String lastDosage, Date lastTakenDate, Boolean isPastVaccinationDateWithinRange) {
        this.patient = patient;
        this.hasActiveSchedule = hasActiveSchedule;
        this.vaccineName = vaccineName;
        this.lastDosage = lastDosage;
        this.lastTakenDate = lastTakenDate;
        this.isPastVaccinationDateWithinRange = isPastVaccinationDateWithinRange;
    }

    public PatientCare careForANCReg() {
        if (hasActiveSchedule || isCareProgramComplete()) return null;
        PatientCare patientCare = createCareFromHistory(patient);
        return patientCare != null ? patientCare : newEnrollmentForCare();
    }

    public PatientCare careForHistory() {
        if (hasActiveSchedule || isCareProgramComplete()) return null;
        PatientCare scheduleForNextTTDose = createCareFromHistory(patient);
        return scheduleForNextTTDose != null ? scheduleForNextTTDose : null;
    }

    private PatientCare createCareFromHistory(Patient patient) {
        if (lastDosage != null && isPastVaccinationDateWithinRange) {
            String nextMilestoneToSchedule = nextVaccineDose(lastDosage);
            return (nextMilestoneToSchedule == null) ? null :
                    PatientCare.forEnrollmentInBetweenProgram(vaccineName, newDate(lastTakenDate), nextMilestoneToSchedule, patient.facilityMetaData());
        }
        return null;
    }

    protected boolean isCareProgramComplete() {
        return (lastDosage != null && nextVaccineDose(lastDosage) == null);
    }

    protected abstract PatientCare newEnrollmentForCare();

    protected abstract String nextVaccineDose(String dosage);

}
