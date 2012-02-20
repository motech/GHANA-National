package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccineDosage;

public class TTVaccineService {

    private EncounterService encounterService;

    public TTVaccineService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void received(TTVaccineDosage dosage, Patient patient, String staffId, String facilityId, LocalDate vaccinationDate) {

    }
}
