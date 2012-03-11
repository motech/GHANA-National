package org.motechproject.ghana.national.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.util.DateUtil;

import static org.motechproject.ghana.national.domain.Constants.NOT_APPLICABLE;

public class TTVaccine {
    private LocalDate vaccinationDate;
    private TTVaccineDosage dosage;
    private Patient patient;

    public TTVaccine(LocalDate vaccinationDate, TTVaccineDosage dosage, Patient patient) {
        this.vaccinationDate = vaccinationDate;
        this.dosage = dosage;
        this.patient = patient;
    }

    public static TTVaccine createFromANCVisit(ANCVisitRequest ancVisit) {
        return (!StringUtils.isEmpty(ancVisit.getTtdose()) && !ancVisit.getTtdose().equals(NOT_APPLICABLE))?
                new TTVaccine(DateUtil.newDate(ancVisit.getDate()), TTVaccineDosage.byValue(Integer.parseInt(ancVisit.getTtdose())), ancVisit.getPatient()): null;
    }

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }

    public TTVaccineDosage getDosage() {
        return dosage;
    }

    public Patient getPatient() {
        return patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TTVaccine)) return false;

        TTVaccine ttVaccine = (TTVaccine) o;

        if (dosage != ttVaccine.dosage) return false;
        if (patient != null ? !patient.equals(ttVaccine.patient) : ttVaccine.patient != null) return false;
        if (vaccinationDate != null ? !vaccinationDate.equals(ttVaccine.vaccinationDate) : ttVaccine.vaccinationDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vaccinationDate != null ? vaccinationDate.hashCode() : 0;
        result = 31 * result + (dosage != null ? dosage.hashCode() : 0);
        result = 31 * result + (patient != null ? patient.hashCode() : 0);
        return result;
    }
}
