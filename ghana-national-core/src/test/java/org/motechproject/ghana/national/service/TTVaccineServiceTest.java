package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.EncounterType;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TTVaccineServiceTest {
    private TTVaccineService ttVaccineService;

    @Mock
    private EncounterService encounterService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineService = new TTVaccineService(encounterService);
    }

    @Test
    @Ignore
    public void shouldCreateEncounterForTTVaccineDosageOneAndCreateScheduleForDosageTwo(){
        Patient patient = new Patient(new MRSPatient("id"));
        String staffId = "staff id";
        String facilityId = "facility id";
        LocalDate vaccinationDate = DateUtil.newDate(2000, 2, 1);
        ttVaccineService.received(TTVaccineDosage.TT1, patient, staffId, facilityId, vaccinationDate);
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        verify(encounterService).persistEncounter(patient.getMrsPatient(), staffId, facilityId, EncounterType.TT_VISIT.name(), vaccinationDate.toDate(), observations);
    }
}
