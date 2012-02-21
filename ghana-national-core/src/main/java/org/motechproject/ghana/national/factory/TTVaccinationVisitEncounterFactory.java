package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.mrs.model.MRSObservation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.TETANUS_TOXOID_DOSE;
import static org.motechproject.ghana.national.domain.EncounterType.TT_VISIT;

public class TTVaccinationVisitEncounterFactory {

    public void createEncounterForVisit(EncounterService encounterService, final TTVaccineDosage dosage, Patient patient, String staffId, String facilityId, LocalDate dateOfObservation) {
        final Date observationDate = dateOfObservation.toDate();
        Set<MRSObservation> observation = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(observationDate, TETANUS_TOXOID_DOSE.getName(), dosage.getDosageAsDouble()));
        }};
        encounterService.persistEncounter(patient.getMrsPatient(), staffId, facilityId, TT_VISIT.value(), observationDate, observation);
    }
}
