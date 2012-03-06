package org.motechproject.ghana.national.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;

import static org.motechproject.ghana.national.domain.Constants.NOT_APPLICABLE;

public class TTVaccine {
    private DateTime vaccinationDate;
    private TTVaccineDosage dosage;
    private Patient patient;

    public TTVaccine(DateTime vaccinationDate, TTVaccineDosage dosage, Patient patient) {
        this.vaccinationDate = vaccinationDate;
        this.dosage = dosage;
        this.patient = patient;
    }

    public static TTVaccine createFromANCVisit(ANCVisitRequest ancVisit) {
        return (!StringUtils.isEmpty(ancVisit.getTtdose()) && !ancVisit.getTtdose().equals(NOT_APPLICABLE))?
                new TTVaccine(new DateTime(ancVisit.getDate()), TTVaccineDosage.byValue(Integer.parseInt(ancVisit.getTtdose())), ancVisit.getPatient()): null;
    }

    public DateTime getVaccinationDate() {
        return vaccinationDate;
    }

    public TTVaccineDosage getDosage() {
        return dosage;
    }

    public Patient getPatient() {
        return patient;
    }

    @Override
    public String toString() {
        return "TTVaccine{" +
                "vaccinationDate=" + vaccinationDate +
                ", dosage=" + dosage +
                ", patient=" + patient +
                '}';
    }
}
