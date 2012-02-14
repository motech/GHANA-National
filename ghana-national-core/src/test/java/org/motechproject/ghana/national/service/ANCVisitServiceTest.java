package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.openmrs.services.OpenMRSConceptAdapter;
import org.motechproject.util.DateUtil;
import org.openmrs.Concept;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ANCVisitServiceTest {
    private ANCVisitService service;
    @Mock
    EncounterService encounterService;
    @Mock
    PatientService patientService;
    @Mock
    OpenMRSConceptAdapter openMRSConceptAdapter;

    @Before
    public void setUp(){
        service = new ANCVisitService();
        initMocks(this);
        ReflectionTestUtils.setField(service, "encounterService", encounterService);
        ReflectionTestUtils.setField(service, "patientService", patientService);
        ReflectionTestUtils.setField(service, "openMRSConceptAdapter", openMRSConceptAdapter);
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
        mrsObservations.add(new MRSObservation<String>("1",new Date(),"Concept-name","Concept-value"));

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
        assertEquals(encounterTypeArgumentCaptor.getValue(), Constants.ENCOUNTER_ANCVISIT);
        assertReflectionEquals(dateArgumentCaptor.getValue(), DateUtil.today().toDate(), ReflectionComparatorMode.LENIENT_DATES);
        assertEquals(setArgumentCaptor.getValue(),mrsObservations);
    }


    @Test
    public void shouldCreateObservationsWithGivenInfo(){
        ANCVisit ancVisit = createTestANCVisit();
        Concept concept_positive = new Concept(703);
        Concept concept_negative = new Concept(664);
        Concept concept_trace = new Concept(6160);
        Concept concept_non_reactive = new Concept(1229);
        Concept concept_reactive = new Concept(1228);

        when(openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_POSITIVE)).thenReturn(concept_positive);
        when(openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NEGATIVE)).thenReturn(concept_negative);
        when(openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_TRACE)).thenReturn(concept_trace);
        when(openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NON_REACTIVE)).thenReturn(concept_non_reactive);
        when(openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_REACTIVE)).thenReturn(concept_reactive);

        Set<MRSObservation> mrsObservations = service.createMRSObservations(ancVisit);
        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();

        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_SERIAL_NUMBER, "4ds65"));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_COMMENTS, "comments"));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_VISIT_NUMBER, "4"));
        expectedObservations.add(new MRSObservation<Integer>(DateUtil.today().toDate(), Constants.CONCEPT_ANC_PNC_LOCATION, 34));
        expectedObservations.add(new MRSObservation<Date>(DateUtil.today().toDate(), Constants.CONCEPT_EDD, new Date(2012, 8, 8)));
        expectedObservations.add(new MRSObservation<Date>(DateUtil.today().toDate(), Constants.CONCEPT_NEXT_ANC_DATE, new Date(2012, 2, 20)));
        expectedObservations.add(new MRSObservation<Integer>(DateUtil.today().toDate(), Constants.CONCEPT_SYSTOLIC_BLOOD_PRESSURE, 10));
        expectedObservations.add(new MRSObservation<Integer>(DateUtil.today().toDate(), Constants.CONCEPT_DIASTOLIC_BLOOD_PRESSURE, 67));
        expectedObservations.add(new MRSObservation<Double>(DateUtil.today().toDate(), Constants.CONCEPT_WEIGHT_KG, 65.67d));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_TT, "4"));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_IPT, "56"));
        expectedObservations.add(new MRSObservation<Concept>(DateUtil.today().toDate(), Constants.CONCEPT_IPT_REACTION, concept_reactive));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE, "itn"));
        expectedObservations.add(new MRSObservation<Double>(DateUtil.today().toDate(), Constants.CONCEPT_FHT, 4.3d));
        expectedObservations.add(new MRSObservation<Integer>(DateUtil.today().toDate(), Constants.CONCEPT_FHR, 4));
        expectedObservations.add(new MRSObservation<Concept>(DateUtil.today().toDate(), Constants.CONCEPT_URINE_GLUCOSE_TEST, concept_negative));
        expectedObservations.add(new MRSObservation<Concept>(DateUtil.today().toDate(), Constants.CONCEPT_URINE_PROTEIN_TEST, concept_positive));
        expectedObservations.add(new MRSObservation<Double>(DateUtil.today().toDate(), Constants.CONCEPT_HEMOGLOBIN, 13.8));
        expectedObservations.add(new MRSObservation<Concept>(DateUtil.today().toDate(), Constants.CONCEPT_VDRL, concept_non_reactive));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_VDRL_TREATMENT, true));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_DEWORMER, true));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_PMTCT, true));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_HIV_PRE_TEST_COUNSELING, false));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_HIV_TEST_RESULT, "hiv"));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_HIV_POST_TEST_COUNSELING, true));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_PMTCT_TREATMENT, true));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_HOUSE, "house"));
        expectedObservations.add(new MRSObservation<String>(DateUtil.today().toDate(), Constants.CONCEPT_COMMUNITY, "community"));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_REFERRED, true));
        expectedObservations.add(new MRSObservation<Boolean>(DateUtil.today().toDate(), Constants.CONCEPT_MALE_INVOLVEMENT, false));

        assertReflectionEquals(expectedObservations, mrsObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }
    

    private ANCVisit createTestANCVisit() {
        ANCVisit ancVisit = new ANCVisit();
        return  (ancVisit.staffId("465").facilityId("232465").motechId("2321465").date(new Date()).serialNumber("4ds65").visitNumber("4").estDeliveryDate(new Date(2012, 8, 8)).
        bpDiastolic(67).bpSystolic(10).weight(65.67d).comments("comments").ttdose("4").iptdose("56").iptReactive("Y").itnUse("itn").fht(4.3d).fhr(4).urineTestGlucosePositive("0").
        urineTestProteinPositive("1").hemoglobin(13.8).vdrlReactive("N").vdrlTreatment("Y").dewormer("Y").pmtct("Y").preTestCounseled("N").
        hivTestResult("hiv").postTestCounseled("Y").pmtctTreament("Y").location("34").house("house").community("community").referred("Y").maleInvolved(false).nextANCDate(new Date(2012, 2, 20)));
    }


}
