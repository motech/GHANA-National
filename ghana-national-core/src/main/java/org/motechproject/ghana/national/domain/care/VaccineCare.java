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
    protected Date lastTakenDate;

    public VaccineCare(Patient patient, Boolean hasActiveSchedule, String vaccineName, String lastDosage, Date lastTakenDate) {
        this.patient = patient;
        this.hasActiveSchedule = hasActiveSchedule;
        this.vaccineName = vaccineName;
        this.lastDosage = lastDosage;
        this.lastTakenDate = lastTakenDate;
    }

    public abstract boolean isWithinActiveRange();

    public PatientCare careForReg() {
        if (hasActiveSchedule || isCareProgramComplete()) return null;
        PatientCare patientCare = createCareFromHistory(patient);
        return patientCare != null ? patientCare : newEnrollmentForCare();
    }

    public PatientCare careForHistory() {
        if (hasActiveSchedule || isCareProgramComplete()) return null;
        PatientCare scheduleForNextVaccineDose = createCareFromHistory(patient);
        return scheduleForNextVaccineDose != null ? scheduleForNextVaccineDose : null;
    }

    private PatientCare createCareFromHistory(Patient patient) {
        if (lastDosage != null && isWithinActiveRange()) {
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
