package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_VISIT;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ChildVisitServiceTest extends BaseUnitTest {
    private ChildVisitService service;
    @Mock
    EncounterService encounterService;
    @Mock
    PatientService patientService;

    @Before
    public void setUp() {
        service = new ChildVisitService();
        initMocks(this);
        ReflectionTestUtils.setField(service, "encounterService", encounterService);
        ReflectionTestUtils.setField(service, "patientService", patientService);
    }

    @Test
    public void shouldCreateEncounterForCWCVisitWithAllInfo() {
        CWCVisit CWCVisit = createTestCWCVisit(new Date());
        ChildVisitService ChildVisitServiceSpy = spy(service);

        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(patientService.getPatientByMotechId(CWCVisit.getMotechId())).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getId()).thenReturn("34");
        when(mockMRSPatient.getMotechId()).thenReturn(CWCVisit.getMotechId());


        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        mrsObservations.add(new MRSObservation<String>("1", new Date(), "MRSConcept-name", "MRSConcept-value"));

        doReturn(mrsObservations).when(ChildVisitServiceSpy).createMRSObservations(CWCVisit);

        ChildVisitServiceSpy.registerCWCVisit(CWCVisit);

        ArgumentCaptor<MRSPatient> mrsPatientArgumentCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        ArgumentCaptor<String> facilityIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> staffIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> encounterTypeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Set> setArgumentCaptor = ArgumentCaptor.forClass(Set.class);

        verify(encounterService).persistEncounter(mrsPatientArgumentCaptor.capture(), staffIdArgumentCaptor.capture(),
                facilityIdArgumentCaptor.capture(), encounterTypeArgumentCaptor.capture(), dateArgumentCaptor.capture(),
                setArgumentCaptor.capture());

        assertEquals(staffIdArgumentCaptor.getValue(), CWCVisit.getStaffId());
        assertEquals(mrsPatientArgumentCaptor.getValue().getMotechId(), CWCVisit.getMotechId());
        assertEquals(facilityIdArgumentCaptor.getValue(), CWCVisit.getFacilityId());
        assertEquals(encounterTypeArgumentCaptor.getValue(), CWC_VISIT.value());
        assertReflectionEquals(dateArgumentCaptor.getValue(), DateUtil.today().toDate(), ReflectionComparatorMode.LENIENT_DATES);
        assertEquals(setArgumentCaptor.getValue(), mrsObservations);
    }

    @Test
    public void shouldCreateObservationsWithGivenInfo() {
        Date today = DateUtil.now().toDate();

        Set<MRSObservation> mrsObservations = service.createMRSObservations(createTestCWCVisit(today));
        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();

        expectedObservations.add(new MRSObservation<String>(today, SERIAL_NUMBER.getName(), "4ds65"));
        expectedObservations.add(new MRSObservation<Boolean>(today, MALE_INVOLVEMENT.getName(), false));
        expectedObservations.add(new MRSObservation<String>(today, COMMENTS.getName(), "comments"));
        expectedObservations.add(new MRSObservation<Integer>(today, CWC_LOCATION.getName(), 34));
        expectedObservations.add(new MRSObservation<Double>(today, WEIGHT_KG.getName(), 65.67d));
        expectedObservations.add(new MRSObservation<String>(today, HOUSE.getName(), "house"));
        expectedObservations.add(new MRSObservation<String>(today, COMMUNITY.getName(), "community"));

        assertReflectionEquals(expectedObservations, mrsObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    private CWCVisit createTestCWCVisit(Date registrationDate) {
        CWCVisit cwcVisit = new CWCVisit();
        return cwcVisit.staffId("465").facilityId("232465").motechId("2321465").date(registrationDate).serialNumber("4ds65")
                .weight(65.67d).comments("comments").cwcLocation("34").house("house").community("community").maleInvolved(false);
    }


}
