package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.factory.PregnancyEncounterFactory;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.service.request.DeliveredChildRequest;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    @Mock
    private IdentifierGenerator mockIdentifierGenerator;

    @Before
    public void setUp() {
        initMocks(this);
        pregnancyService = new PregnancyService(mockAllPatients, mockAllEncounters, mockAllSchedules, mockAllAppointments, mockIdentifierGenerator);
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

        Encounter expectedEncounter = new PregnancyEncounterFactory().createTerminationEncounter(request);
        ArgumentCaptor<Encounter> argumentCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(argumentCaptor.capture());

        assertReflectionEquals(expectedEncounter, argumentCaptor.getValue());
    }

    @Test
    public void shouldCreateEncounterForPregnancyDelivery() {
        MRSFacility mrsFacility = new MRSFacility("12");
        Facility mockFacility = new Facility(mrsFacility);
        MRSUser mockStaff = mock(MRSUser.class);
        String parentMotechId = "121";
        MRSPatient mrsPatient = new MRSPatient(parentMotechId, null, null);
        Patient mockPatient = new Patient(mrsPatient, parentMotechId);
        PregnancyDeliveryRequest deliveryRequest = pregnancyDelivery(mockPatient, mockStaff, mockFacility);
        pregnancyService.handleDelivery(deliveryRequest);

        Encounter expectedEncounter = new PregnancyEncounterFactory().createDeliveryEncounter(deliveryRequest);
        ArgumentCaptor<Encounter> encounterArgumentCaptor = ArgumentCaptor.forClass(Encounter.class);
        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(mockAllEncounters).persistEncounter(encounterArgumentCaptor.capture());
        verify(mockAllPatients).save(patientArgumentCaptor.capture());

        assertReflectionEquals(expectedEncounter, encounterArgumentCaptor.getValue());
        Patient actualChild = patientArgumentCaptor.getValue();
        assertThat(actualChild.getMotechId(), is(deliveryRequest.getDeliveredChildRequests().get(0).getchildMotechId()));
        assertThat(actualChild.getParentId(), is(parentMotechId));
        assertThat(actualChild.getFirstName(), is("Jo"));
        assertThat(actualChild.getLastName(), is("Baby"));
        assertThat(actualChild.getMrsPatient().getFacility(), is(mrsFacility));
        verify(mockAllSchedules).unEnroll(mrsPatient.getId(), mockPatient.careProgramsToUnEnroll());
        verify(mockAllAppointments).remove(mockPatient);
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

    private PregnancyDeliveryRequest pregnancyDelivery(Patient mockPatient, MRSUser mockUser, Facility mockFacility) {
        PregnancyDeliveryRequest request = new PregnancyDeliveryRequest();
        request.facility(mockFacility);
        request.patient(mockPatient);
        request.staff(mockUser);
        request.deliveryDateTime(DateTime.now());
        request.childDeliveryOutcome(ChildDeliveryOutcome.SINGLETON);
        request.addDeliveredChildRequest(new DeliveredChildRequest()
                .childBirthOutcome(BirthOutcome.ALIVE)
                .childRegistrationType(RegistrationType.USE_PREPRINTED_ID).childFirstName("Jo"));
        return request;
    }
}
