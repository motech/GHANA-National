package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.factory.PregnancyTerminationEncounterFactory;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.OTHER_CAUSE_OF_DEATH;
import static org.motechproject.ghana.national.domain.Constants.PREGNANCY_TERMINATION;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PregnancyServiceTest {
    private PregnancyService pregnancyService;
    @Mock
    private AllPatients mockAllPatients;
    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private AllSchedules mockAllSchedules;
    @Mock
    private AllAppointments mockAllAppointments;

    @Before
    public void setUp() {
        initMocks(this);
        pregnancyService = new PregnancyService(mockAllPatients, mockAllEncounters, mockAllSchedules, mockAllAppointments);
    }

    @Test
    public void shouldDeceasePatientIfDeadDuringPregnancyTermination() {
        String mrsFacilityId = "mrsFacilityId";
        String patientMRSId = "patientMRSId";
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(mockMRSPatient.getId()).thenReturn("mrsPatientId");
        Facility mockFacility = mock(Facility.class);
        MRSUser mockStaff = mock(MRSUser.class);
        List<String> schedules = Arrays.asList("schedule1", "schedule2");
        PregnancyTerminationRequest request = pregnancyTermination(mockPatient, mockStaff, mockFacility);
        request.setDead(Boolean.TRUE);

        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockPatient.getMRSPatientId()).thenReturn(patientMRSId);
        when(mockPatient.careProgramsToUnEnroll()).thenReturn(schedules);
        when(mockFacility.getMrsFacilityId()).thenReturn(mrsFacilityId);

        pregnancyService.terminatePregnancy(request);

        verify(mockAllPatients).deceasePatient(request.getTerminationDate(), request.getPatient().getMotechId(), OTHER_CAUSE_OF_DEATH, PREGNANCY_TERMINATION);
        verify(mockAllSchedules).unEnroll(patientMRSId, schedules);
        verify(mockAllAppointments).remove(mockPatient);
    }

    @Test
    public void shouldCreateEncounterOnPregnancyTermination() {
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        Facility mockFacility = mock(Facility.class);
        MRSUser mockStaff = mock(MRSUser.class);

        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        final Date date = DateUtil.newDate(2000, 12, 12).toDate();
        PregnancyTerminationRequest request = pregnancyTermination(mockPatient, mockStaff, mockFacility);
        request.setTerminationDate(date);

        pregnancyService.terminatePregnancy(request);
//        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);

        Encounter expectedEncounter = new PregnancyTerminationEncounterFactory().createEncounter(request);
        ArgumentCaptor<Encounter> argumentCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(argumentCaptor.capture());

        assertReflectionEquals(expectedEncounter, argumentCaptor.getValue());
//        Set observations = captor.getValue();

//        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
//            add(new MRSObservation<Boolean>(date, PREGNANCY_STATUS.getName(), false));
//            add(new MRSObservation<Integer>(date, TERMINATION_TYPE.getName(), 2));
//            add(new MRSObservation<Integer>(date, TERMINATION_PROCEDURE.getName(), 1));
//            add(new MRSObservation<Integer>(date, TERMINATION_COMPLICATION.getName(), 1));
//            add(new MRSObservation<Integer>(date, TERMINATION_COMPLICATION.getName(), 2));
//            add(new MRSObservation<Boolean>(date, MATERNAL_DEATH.getName(), false));
//            add(new MRSObservation<Boolean>(date, REFERRED.getName(), false));
//            add(new MRSObservation<String>(date, COMMENTS.getName(), "Patient lost lot of blood"));
//            add(new MRSObservation<Boolean>(date, POST_ABORTION_FP_COUNSELING.getName(), Boolean.FALSE));
//            add(new MRSObservation<Boolean>(date, POST_ABORTION_FP_ACCEPTED.getName(), Boolean.TRUE));
//        }};
//
//        assertReflectionEquals(expectedObservations, observations, ReflectionComparatorMode.LENIENT_ORDER);
    }

    private PregnancyTerminationRequest pregnancyTermination(Patient mockPatient, MRSUser mockUser, Facility mockFacility) {
        PregnancyTerminationRequest request = new PregnancyTerminationRequest();
        request.setFacility(mockFacility);
        request.setPatient(mockPatient);
        request.setStaff(mockUser);
        request.setDead(Boolean.FALSE);
        request.setReferred(Boolean.FALSE);
        request.setTerminationProcedure("1");
        request.setTerminationType("2");
        request.setComments("Patient lost lot of blood");
        request.addComplication("1");
        request.addComplication("2");
        request.setPostAbortionFPAccepted(Boolean.TRUE);
        request.setPostAbortionFPCounselling(Boolean.FALSE);
        return request;
    }

}
