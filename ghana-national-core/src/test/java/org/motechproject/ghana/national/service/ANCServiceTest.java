package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.mrs.model.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ANCServiceTest {
    private ANCService ancService;

    @Mock
    StaffService mockStaffService;
    @Mock
    PatientService mockPatientService;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    AllEncounters mockAllEncounters;

    @Before
    public void setUp() {
        initMocks(this);
        ancService = new ANCService();
        ReflectionTestUtils.setField(ancService, "staffService", mockStaffService);
        ReflectionTestUtils.setField(ancService, "patientService", mockPatientService);
        ReflectionTestUtils.setField(ancService, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(ancService, "allEncounters", mockAllEncounters);
    }

    @Test
    public void shouldEnrollANC() throws Exception {
        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5));
        final Date observationDate = new Date();
        MRSUser mockMRSUser = mock(MRSUser.class);
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        Facility mockFacility = mock(Facility.class);
        MRSFacility mockMRSFacility = mock(MRSFacility.class);
        MRSPerson mockMRSPerson = mock(MRSPerson.class);

        when(mockStaffService.getUserById(ancvo.getStaffId())).thenReturn(mockMRSUser);
        when(mockPatientService.getPatientByMotechId(ancvo.getMotechPatientId())).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockFacilityService.getFacilityByMotechId(ancvo.getFacilityId())).thenReturn(mockFacility);
        when(mockFacility.mrsFacility()).thenReturn(mockMRSFacility);
        when(mockMRSUser.getPerson()).thenReturn(mockMRSPerson);

        ancService.enroll(ancvo);

        ArgumentCaptor<MRSEncounter> captor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(captor.capture());
        MRSEncounter actualEncounter = captor.getValue();
        assertEquals(mockMRSPatient, actualEncounter.getPatient());
        assertEquals(mockMRSUser, actualEncounter.getCreator());
        assertEquals(mockMRSFacility, actualEncounter.getFacility());
        assertEquals(mockMRSPerson, actualEncounter.getProvider());
        assertEquals(8, actualEncounter.getObservations().size());
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(observationDate, ANCService.CONCEPT_GRAVIDA, ancvo.getGravida()));
            add(new MRSObservation<Double>(observationDate, ANCService.CONCEPT_HEIGHT, ancvo.getHeight()));
            add(new MRSObservation<Integer>(observationDate, ANCService.CONCEPT_PARITY, ancvo.getParity()));
            add(new MRSObservation<Date>(observationDate, ANCService.CONCEPT_EDD, ancvo.getEstimatedDateOfDelivery()));
            add(new MRSObservation<Boolean>(observationDate, ANCService.CONCEPT_CONFINEMENT_CONFIRMED, ancvo.getDeliveryDateConfirmed()));
            add(new MRSObservation<String>(observationDate, ANCService.CONCEPT_ANC_REG_NUM, ancvo.getSerialNumber()));
            add(new MRSObservation<Integer>(ancvo.getLastIPTDate(), ANCService.CONCEPT_IPT, Integer.valueOf(ancvo.getLastIPT())));
            add(new MRSObservation<Integer>(ancvo.getLastTTDate(), ANCService.CONCEPT_TT, Integer.valueOf(ancvo.getLastTT())));
        }};

        assertReflectionEquals(actualEncounter.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldNotAddObsIfValueNotGiven() {
        final ANCVO ancvo = createTestANCVO(null, null, null, null);
        final Date observationDate = new Date();
        MRSUser mockMRSUser = mock(MRSUser.class);
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        Facility mockFacility = mock(Facility.class);
        MRSFacility mockMRSFacility = mock(MRSFacility.class);
        MRSPerson mockMRSPerson = mock(MRSPerson.class);

        when(mockStaffService.getUserById(ancvo.getStaffId())).thenReturn(mockMRSUser);
        when(mockPatientService.getPatientByMotechId(ancvo.getMotechPatientId())).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockFacilityService.getFacilityByMotechId(ancvo.getFacilityId())).thenReturn(mockFacility);
        when(mockFacility.mrsFacility()).thenReturn(mockMRSFacility);
        when(mockMRSUser.getPerson()).thenReturn(mockMRSPerson);

        ancService.enroll(ancvo);

        ArgumentCaptor<MRSEncounter> captor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(captor.capture());
        MRSEncounter actualEncounter = captor.getValue();
        assertEquals(mockMRSPatient, actualEncounter.getPatient());
        assertEquals(mockMRSUser, actualEncounter.getCreator());
        assertEquals(mockMRSFacility, actualEncounter.getFacility());
        assertEquals(mockMRSPerson, actualEncounter.getProvider());
        assertEquals(6, actualEncounter.getObservations().size());
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(observationDate, ANCService.CONCEPT_GRAVIDA, ancvo.getGravida()));
            add(new MRSObservation<Double>(observationDate, ANCService.CONCEPT_HEIGHT, ancvo.getHeight()));
            add(new MRSObservation<Integer>(observationDate, ANCService.CONCEPT_PARITY, ancvo.getParity()));
            add(new MRSObservation<Date>(observationDate, ANCService.CONCEPT_EDD, ancvo.getEstimatedDateOfDelivery()));
            add(new MRSObservation<Boolean>(observationDate, ANCService.CONCEPT_CONFINEMENT_CONFIRMED, ancvo.getDeliveryDateConfirmed()));
            add(new MRSObservation<String>(observationDate, ANCService.CONCEPT_ANC_REG_NUM, ancvo.getSerialNumber()));
        }};

        assertReflectionEquals(actualEncounter.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES, ReflectionComparatorMode.LENIENT_ORDER);

    }

    private ANCVO createTestANCVO(String ipt, Date iptDate, String tt, Date ttDate) {
        return new ANCVO("12345", "3434343", "1213343", new Date(), RegistrationToday.IN_PAST, "2321322", new Date(),
                12.34, 12, 34, true, true, new ArrayList<ANCCareHistory>(), ipt, tt, iptDate, ttDate);
    }

}
