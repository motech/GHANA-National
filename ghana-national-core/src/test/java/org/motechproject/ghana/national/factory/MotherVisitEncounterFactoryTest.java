package org.motechproject.ghana.national.factory;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.*;
import org.motechproject.util.DateUtil;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class MotherVisitEncounterFactoryTest {
    private MotherVisitEncounterFactory factory;

    @Before
    public void setUp() {
        factory = new MotherVisitEncounterFactory();
    }

    @Test
    public void shouldCreateEncounterForAncVisit() {
        Patient patient = mock(Patient.class);
        String mrsFacilityId = "mrsFacilityId";
        Facility facility = new Facility(new MRSFacility(mrsFacilityId));
        facility.mrsFacilityId(mrsFacilityId);
        MRSUser staff = new MRSUser();
        ANCVisit ancVisit = createTestANCVisit(staff, facility, patient);
        MRSConcept conceptPositive = new MRSConcept(POSITIVE.getName());
        MRSConcept conceptNegative = new MRSConcept(NEGATIVE.getName());
        MRSConcept conceptNonReactive = new MRSConcept(NON_REACTIVE.getName());
        MRSConcept conceptReactive = new MRSConcept(REACTIVE.getName());

        Set<MRSObservation> mrsObservations = factory.createMRSObservations(ancVisit);
        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();

        Date today = DateUtil.today().toDate();
        expectedObservations.add(new MRSObservation<String>(today, SERIAL_NUMBER.getName(), "4ds65"));
        expectedObservations.add(new MRSObservation<Integer>(today, VISIT_NUMBER.getName(), 4));
        expectedObservations.add(new MRSObservation<Boolean>(today, MALE_INVOLVEMENT.getName(), false));
        expectedObservations.add(new MRSObservation<Date>(today, EDD.getName(), DateUtil.newDate(2012, 8, 8).toDate()));
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

    private ANCVisit createTestANCVisit(MRSUser staff, Facility facility, Patient patient) {
        return new ANCVisit().staff(staff).facility(facility).patient(patient).date(new Date()).serialNumber("4ds65")
                .visitNumber("4").estDeliveryDate(DateUtil.newDate(2012, 8, 8).toDate())
                .bpDiastolic(67).bpSystolic(10).weight(65.67d).comments("comments").ttdose("4").iptdose("5")
                .iptReactive("Y").itnUse("Y").fht(4.3d).fhr(4).urineTestGlucosePositive("0").urineTestProteinPositive("1")
                .hemoglobin(13.8).vdrlReactive("N").vdrlTreatment(null).dewormer("Y").pmtct("Y").preTestCounseled("N")
                .hivTestResult("hiv").postTestCounseled("Y").pmtctTreament("Y").location("34").house("house").community("community")
                .referred("Y").maleInvolved(false).nextANCDate(DateUtil.newDate(2012, 2, 20).toDate());
    }

}
