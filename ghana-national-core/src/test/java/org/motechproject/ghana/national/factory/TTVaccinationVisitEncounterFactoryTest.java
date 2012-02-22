package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Concept.TETANUS_TOXOID_DOSE;
import static org.motechproject.ghana.national.domain.EncounterType.TT_VISIT;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT1;

public class TTVaccinationVisitEncounterFactoryTest {
    @Mock
    private EncounterService encounterService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldCreateEncounterForTTVisit(){
        String staffId = "staff id";
        String facilityId = "facility id";
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final LocalDate vaccinationDate = DateUtil.newDate(2000, 2, 1);

        new TTVaccinationVisitEncounterFactory().createEncounterForVisit(encounterService, TT1, patient, staffId, facilityId, vaccinationDate);
        final Set<MRSObservation> observations = new HashSet<MRSObservation>(){{
            add(new MRSObservation<Double>(vaccinationDate.toDate(), TETANUS_TOXOID_DOSE.getName(), TT1.getDosageAsDouble()));
        }};
        verify(encounterService).persistEncounter(patient.getMrsPatient(), staffId, facilityId, TT_VISIT.value(), vaccinationDate.toDate(), observations);

    }
}
