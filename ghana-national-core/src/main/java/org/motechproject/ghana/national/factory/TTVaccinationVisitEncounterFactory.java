package org.motechproject.ghana.national.factory;

import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.TTVisit;
import org.motechproject.mrs.model.MRSObservation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.TETANUS_TOXOID_DOSE;
import static org.motechproject.ghana.national.domain.EncounterType.TT_VISIT;

public class TTVaccinationVisitEncounterFactory {

    public Encounter createEncounterForVisit(final TTVisit ttVisit) {
        final Date observationDate = ttVisit.getDate();
        Set<MRSObservation> observations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(observationDate, TETANUS_TOXOID_DOSE.getName(), ttVisit.getDosage().getDosageAsDouble()));
        }};
        return new Encounter(ttVisit.getPatient().getMrsPatient(), ttVisit.getStaff(), ttVisit.getFacility(), TT_VISIT, observationDate, observations);
    }
}
