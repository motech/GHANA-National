package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION_VISIT;
import static org.motechproject.ghana.national.domain.Concept.TETANUS_TOXOID_DOSE;
import static org.motechproject.ghana.national.domain.EncounterType.TT_VISIT;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT2;

public class TTVaccineServiceTest {
    private TTVaccineService ttVaccineServiceSpy;

    @Mock
    private EncounterService encounterService;
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineServiceSpy = spy(new TTVaccineService(encounterService, scheduleTrackingService));
    }

    @Test
    public void shouldCreateEncounter_EnrollPatientForCurrentScheduleAndCreateSchedulesForTheNext(){
        String staffId = "staff id";
        String facilityId = "facility id";
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final LocalDate vaccinationDate = DateUtil.newDate(2000, 2, 1);

        when(scheduleTrackingService.getEnrollment(patientId, TT_VACCINATION_VISIT)).thenReturn(null);

        ttVaccineServiceSpy.received(TT2, patient, staffId, facilityId, vaccinationDate);
        final Set<MRSObservation> observations = new HashSet<MRSObservation>(){{
            add(new MRSObservation<Double>(vaccinationDate.toDate(), TETANUS_TOXOID_DOSE.getName(), TT2.getDosageAsDouble()));
        }};

        verify(encounterService).persistEncounter(patient.getMrsPatient(), staffId, facilityId, TT_VISIT.value(), vaccinationDate.toDate(), observations);
        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(ttVaccineServiceSpy).scheduleAlerts(eq(patient), enrollmentRequestCaptor.capture());

        EnrollmentRequest enrollmentRequest = enrollmentRequestCaptor.getValue();
        assertThat(enrollmentRequest.getScheduleName(), is(equalTo(TT_VACCINATION_VISIT)));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(TT2.name())));
        assertThat(enrollmentRequest.getReferenceDate(),is(equalTo(vaccinationDate)));


    }

}
