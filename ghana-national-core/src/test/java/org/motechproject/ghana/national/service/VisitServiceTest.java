package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.domain.EncounterType.TT_VISIT;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT2;

public class VisitServiceTest {

    private VisitService visitService;

    @Mock
    private AllSchedules allSchedules;

    @Mock
    private AllEncounters allEncounters;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        visitService = new VisitService(allSchedules, allEncounters);
    }

    @Test
    public void shouldEnrollPatientForCurrentScheduleAndCreateSchedulesForTheNextTTVisit() {
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final DateTime vaccinationDate = DateUtil.newDateTime(2000, 2, 1, new Time(10, 10));
        visitService.createTTSchedule(new TTVaccine(vaccinationDate, TT2, patient));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        ArgumentCaptor<LocalDate> fulfillmentDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(allSchedules).enrollOrFulfill(enrollmentRequestCaptor.capture(), fulfillmentDateCaptor.capture());
        assertThat(fulfillmentDateCaptor.getValue(), is(vaccinationDate.toLocalDate()));

        EnrollmentRequest enrollmentRequest = enrollmentRequestCaptor.getValue();
        assertThat(enrollmentRequest.getScheduleName(), is(equalTo(TT_VACCINATION)));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(TT2.name())));
        assertThat(enrollmentRequest.getReferenceDate(), is(equalTo(vaccinationDate.toLocalDate())));
    }

    @Test
    public void shouldCreateEncounterAndScheduleForTTVisit(){
        final VisitService visitServiceSpy = spy(visitService);
        MRSUser staff = new MRSUser();
        Facility facility = new Facility();
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final DateTime vaccinationDate = DateUtil.newDateTime(2000, 2, 1, new Time(10,10));
        final TTVaccine ttVaccine = new TTVaccine(vaccinationDate, TT2, patient);
        visitServiceSpy.receivedTT(ttVaccine, staff, facility);
        ArgumentCaptor<Encounter> encounterCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(allEncounters).persistEncounter(encounterCaptor.capture());
        assertThat(encounterCaptor.getValue().getType(), is(equalTo(TT_VISIT.value())));
        verify(visitServiceSpy).createTTSchedule(ttVaccine);
    }


}
