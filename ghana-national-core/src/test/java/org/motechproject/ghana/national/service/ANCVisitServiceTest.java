package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSConcept;
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
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ANCVisitServiceTest extends BaseUnitTest {
    private ANCVisitService service;
    @Mock
    EncounterService encounterService;
    @Mock
    PatientService patientService;
    @Mock
    AllObservations mockAllObservations;
    @Mock
    MotherVisitService mockMotherVisitService;

    @Before
    public void setUp(){
        service = new ANCVisitService();
        initMocks(this);
        ReflectionTestUtils.setField(service, "encounterService", encounterService);
        ReflectionTestUtils.setField(service, "patientService", patientService);
        ReflectionTestUtils.setField(service, "allObservations", mockAllObservations);
        ReflectionTestUtils.setField(service, "motherVisitService", mockMotherVisitService);
    }
    
    @Test
    public void shouldCreateEncounterForANCVisitWithAllInfo(){
        ANCVisit ancVisit = createTestANCVisit();
        ANCVisitService ancVisitServiceSpy = spy(service);

        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        when(patientService.getPatientByMotechId(ancVisit.getMotechId())).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getId()).thenReturn("34");
        when(mockMRSPatient.getMotechId()).thenReturn(ancVisit.getMotechId());


        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        mrsObservations.add(new MRSObservation<String>("1",new Date(),"MRSConcept-name","MRSConcept-value"));

        doReturn(mrsObservations).when(ancVisitServiceSpy).createMRSObservations(ancVisit);

        ancVisitServiceSpy.registerANCVisit(ancVisit);

        ArgumentCaptor<MRSPatient> mrsPatientArgumentCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        ArgumentCaptor<String> facilityIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> staffIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> encounterTypeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Set> setArgumentCaptor = ArgumentCaptor.forClass(Set.class);

        verify(encounterService).persistEncounter(mrsPatientArgumentCaptor.capture(), staffIdArgumentCaptor.capture(),
                            facilityIdArgumentCaptor.capture(), encounterTypeArgumentCaptor.capture(), dateArgumentCaptor.capture(),
                            setArgumentCaptor.capture());

        assertEquals(staffIdArgumentCaptor.getValue(),ancVisit.getStaffId());
        assertEquals(mrsPatientArgumentCaptor.getValue().getMotechId(),ancVisit.getMotechId());
        assertEquals(facilityIdArgumentCaptor.getValue(), ancVisit.getFacilityId());
        assertEquals(encounterTypeArgumentCaptor.getValue(), ANC_VISIT.value());
        assertReflectionEquals(dateArgumentCaptor.getValue(), DateUtil.today().toDate(), ReflectionComparatorMode.LENIENT_DATES);
        assertEquals(setArgumentCaptor.getValue(),mrsObservations);
    }
    
    @Test
    public void shouldRescheduleEDDIfItIsModified() {
        ANCVisit ancVisit = createTestANCVisit();
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        when(edd.getValue()).thenReturn(DateUtil.newDate(2010, 12,11).toDate());
        Patient mockPatient = mock(Patient.class);
        when(patientService.getPatientByMotechId(ancVisit.getMotechId())).thenReturn(mockPatient);
        when(mockAllObservations.findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockAllObservations.findObservation(ancVisit.getMotechId(), Concept.EDD.getName())).thenReturn(edd);

        service.registerANCVisit(ancVisit);

        verify(mockAllObservations).findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName());
        verify(mockAllObservations).voidObservation(eq(edd), anyString(), eq(ancVisit.getStaffId()));
        verify(mockMotherVisitService).createEDDScheduleForANCVisit(mockPatient,ancVisit.getEstDeliveryDate());
    }

    @Test
    public void shouldNotRescheduleEDDIfEddIsLeftEmpty() {
        ANCVisit ancVisit = createTestANCVisit();
        ancVisit.estDeliveryDate(null);
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        when(edd.getValue()).thenReturn(DateUtil.newDate(2010, 12,11).toDate());
        Patient mockPatient = mock(Patient.class);
        when(patientService.getPatientByMotechId(ancVisit.getMotechId())).thenReturn(mockPatient);
        when(mockAllObservations.findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockAllObservations.findObservation(ancVisit.getMotechId(), Concept.EDD.getName())).thenReturn(edd);

        service.registerANCVisit(ancVisit);

        verify(mockAllObservations, never()).findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName());
        verify(mockAllObservations, never()).voidObservation(eq(edd), anyString(), eq(ancVisit.getStaffId()));
        verify(mockMotherVisitService, never()).createEDDScheduleForANCVisit(mockPatient,ancVisit.getEstDeliveryDate());
    }

    @Test
    public void shouldNotRescheduleEDDIfEddIsNotModified() {
        ANCVisit ancVisit = createTestANCVisit();
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        when(edd.getValue()).thenReturn(ancVisit.getEstDeliveryDate());
        Patient mockPatient = mock(Patient.class);
        when(patientService.getPatientByMotechId(ancVisit.getMotechId())).thenReturn(mockPatient);
        when(mockAllObservations.findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockAllObservations.findObservation(ancVisit.getMotechId(), Concept.EDD.getName())).thenReturn(edd);

        service.registerANCVisit(ancVisit);

        verify(mockAllObservations).findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName());
        verify(mockAllObservations, never()).voidObservation(eq(edd), anyString(), eq(ancVisit.getStaffId()));
        verify(mockMotherVisitService, never()).createEDDScheduleForANCVisit(mockPatient,ancVisit.getEstDeliveryDate());
    }

    @Test
    public void shouldCreateObservationsWithGivenInfo(){
        ANCVisit ancVisit = createTestANCVisit();
        MRSConcept conceptPositive = new MRSConcept(POSITIVE.getName());
        MRSConcept conceptNegative = new MRSConcept(NEGATIVE.getName());
        MRSConcept conceptNonReactive = new MRSConcept(NON_REACTIVE.getName());
        MRSConcept conceptReactive = new MRSConcept(REACTIVE.getName());

        Set<MRSObservation> mrsObservations = service.createMRSObservations(ancVisit);
        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();

        Date today = DateUtil.today().toDate();
        expectedObservations.add(new MRSObservation<String>(today, SERIAL_NUMBER.getName(), "4ds65"));
        expectedObservations.add(new MRSObservation<Integer>(today, VISIT_NUMBER.getName(), 4));
        expectedObservations.add(new MRSObservation<Boolean>(today, MALE_INVOLVEMENT.getName(), false));
        expectedObservations.add(new MRSObservation<String>(today, COMMENTS.getName(), "comments"));
        expectedObservations.add(new MRSObservation<Integer>(today, ANC_PNC_LOCATION.getName(), 34));
        expectedObservations.add(new MRSObservation<Date>(today, NEXT_ANC_DATE.getName(), DateUtil.newDate(2012, 2, 20).toDate()));
        expectedObservations.add(new MRSObservation<Integer>(today, SYSTOLIC_BLOOD_PRESSURE.getName(), 10));
        expectedObservations.add(new MRSObservation<Integer>(today, DIASTOLIC_BLOOD_PRESSURE.getName(), 67));
        expectedObservations.add(new MRSObservation<Double>(today, WEIGHT_KG.getName(), 65.67d));
        expectedObservations.add(new MRSObservation<Integer>(today, TT.getName(), 4));
        expectedObservations.add(new MRSObservation<Integer>(today, IPT.getName(), 5));
        expectedObservations.add(new MRSObservation<MRSConcept>(today, IPT_REACTION.getName(), conceptReactive));
        expectedObservations.add(new MRSObservation<Boolean>(today, INSECTICIDE_TREATED_NET_USAGE.getName(), true));
        expectedObservations.add(new MRSObservation<Double>(today, FHT.getName(), 4.3d));
        expectedObservations.add(new MRSObservation<Integer>(today, FHR.getName(), 4));
        expectedObservations.add(new MRSObservation<MRSConcept>(today, URINE_GLUCOSE_TEST.getName(), conceptNegative));
        expectedObservations.add(new MRSObservation<MRSConcept>(today, URINE_PROTEIN_TEST.getName(), conceptPositive));
        expectedObservations.add(new MRSObservation<Double>(today, HEMOGLOBIN.getName(), 13.8));
        expectedObservations.add(new MRSObservation<MRSConcept>(today, VDRL.getName(), conceptNonReactive));
        expectedObservations.add(new MRSObservation<Boolean>(today, DEWORMER.getName(), true));
        expectedObservations.add(new MRSObservation<Boolean>(today, PMTCT.getName(), true));
        expectedObservations.add(new MRSObservation<Boolean>(today, HIV_PRE_TEST_COUNSELING.getName(), false));
        expectedObservations.add(new MRSObservation<String>(today, HIV_TEST_RESULT.getName(), "hiv"));
        expectedObservations.add(new MRSObservation<Boolean>(today, HIV_POST_TEST_COUNSELING.getName(), true));
        expectedObservations.add(new MRSObservation<Boolean>(today, PMTCT_TREATMENT.getName(), true));
        expectedObservations.add(new MRSObservation<String>(today, HOUSE.getName(), "house"));
        expectedObservations.add(new MRSObservation<String>(today, COMMUNITY.getName(), "community"));
        expectedObservations.add(new MRSObservation<Boolean>(today, REFERRED.getName(), true));

        assertReflectionEquals(expectedObservations, mrsObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    private ANCVisit createTestANCVisit() {
        ANCVisit ancVisit = new ANCVisit();
        return  (ancVisit.staffId("465").facilityId("232465").motechId("2321465").date(new Date()).serialNumber("4ds65").visitNumber("4").estDeliveryDate(new Date(2012, 8, 8)).
        bpDiastolic(67).bpSystolic(10).weight(65.67d).comments("comments").ttdose("4").iptdose("5").iptReactive("Y").itnUse("Y").fht(4.3d).fhr(4).urineTestGlucosePositive("0").
        urineTestProteinPositive("1").hemoglobin(13.8).vdrlReactive("N").vdrlTreatment(null).dewormer("Y").pmtct("Y").preTestCounseled("N").
        hivTestResult("hiv").postTestCounseled("Y").pmtctTreament("Y").location("34").house("house").community("community").referred("Y").maleInvolved(false).nextANCDate(new Date(2012, 2, 20)));
    }


}
