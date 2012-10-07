package org.motechproject.ghana.national.factory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.ANCVisit;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.*;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_DATES;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

public class MotherVisitEncounterFactoryTest {
    private MotherVisitEncounterFactory factory;

    @Before
    public void setUp() {
        factory = new MotherVisitEncounterFactory();
    }

    @Test
    public void shouldCreateEncounterForAncVisit() {
        ANCVisit ancVisit = createTestANCVisit();
        MRSConcept conceptPositive = new MRSConcept(POSITIVE.getName());
        MRSConcept conceptNegative = new MRSConcept(NEGATIVE.getName());
        MRSConcept conceptNonReactive = new MRSConcept(NON_REACTIVE.getName());

        Set<MRSObservation> mrsObservations = factory.createMRSObservations(ancVisit);
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

        assertReflectionEquals(expectedObservations, mrsObservations, LENIENT_DATES,
                LENIENT_ORDER);
    }

    @Test
    public void shouldCreateObservationsForIPT() {
        IPTVaccine iptVaccine = new IPTVaccine(DateUtil.newDate(2012, 1, 2), null, IPTDose.SP1, IPTReaction.REACTIVE);
        Set<MRSObservation> actualObservations = factory.createObservationsForIPT(iptVaccine);

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();
        expectedObservations.add(new MRSObservation<Integer>(iptVaccine.getVaccinationDate().toDate(), Concept.IPT.getName(), iptVaccine.getIptDose()));
        expectedObservations.add(new MRSObservation<MRSConcept>(iptVaccine.getVaccinationDate().toDate(), IPT_REACTION.getName(),
                new MRSConcept(iptVaccine.getIptReactionConceptName())));
        assertReflectionEquals(expectedObservations, actualObservations, LENIENT_ORDER);
    }

    @Test
    public void shouldCreateObservationsForTT() {
        TTVaccine ttVaccine = new TTVaccine(DateUtil.newDateTime(2012, 1, 2, new Time(10, 10)), TTVaccineDosage.TT1, null);
        Set<MRSObservation> actualObservations = factory.createObservationForTT(ttVaccine);

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();
        expectedObservations.add(new MRSObservation<Integer>(ttVaccine.getVaccinationDate().toDate(), Concept.TT.getName(), 1));
        assertReflectionEquals(expectedObservations, actualObservations, LENIENT_ORDER);
    }

    @Test
    public void shouldCreatePNCMotherEncounterForPNCMotherVisit() {
        String comment = "comment";
        String community = "community";
        DateTime date = DateUtil.now();
        String fht = "120";
        String house = "house";
        String location = "2";
        Boolean lochiaAmountExcess = Boolean.TRUE;
        String lochiaColour = "1";
        Boolean lochiaOdourFoul = Boolean.TRUE;
        Boolean maleInvolved = Boolean.TRUE;
        Patient patient = new Patient(new MRSPatient("motechId"));
        double temperature = 20D;
        int visitNumber = 1;
        String vitaminA = "NA";
        MRSUser staff = new MRSUser();
        Facility facility = new Facility();
        Date registrationDate = date.toDate();

        PNCMotherRequest pncMotherRequest = createPNCMotherRequest(comment, community, date, facility, staff, fht, house, location, lochiaAmountExcess, lochiaColour, lochiaOdourFoul, maleInvolved, patient, temperature, visitNumber, vitaminA);

        Encounter encounter = factory.createEncounter(pncMotherRequest);
        Set<MRSObservation> mrsObservations = encounter.getObservations();

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();

        expectedObservations.add(new MRSObservation<Integer>(registrationDate, TT.getName(), 1));
        expectedObservations.add(new MRSObservation<Integer>(registrationDate, VISIT_NUMBER.getName(), 1));
        expectedObservations.add(new MRSObservation<Boolean>(registrationDate, VITA.getName(), false));
        expectedObservations.add(new MRSObservation<String>(registrationDate, COMMUNITY.getName(), community));
        expectedObservations.add(new MRSObservation<Integer>(registrationDate, FHT.getName(), 120));
        expectedObservations.add(new MRSObservation<String>(registrationDate, COMMENTS.getName(), comment));
        expectedObservations.add(new MRSObservation<String>(registrationDate, HOUSE.getName(), house));
        expectedObservations.add(new MRSObservation<Boolean>(registrationDate, MALE_INVOLVEMENT.getName(), maleInvolved));
        expectedObservations.add(new MRSObservation<Boolean>(registrationDate, REFERRED.getName(), Boolean.TRUE));
        expectedObservations.add(new MRSObservation<Boolean>(registrationDate, LOCHIA_EXCESS_AMOUNT.getName(), lochiaAmountExcess));
        expectedObservations.add(new MRSObservation<Integer>(registrationDate, LOCHIA_COLOUR.getName(), 1));
        expectedObservations.add(new MRSObservation<Boolean>(registrationDate, LOCHIA_FOUL_ODOUR.getName(), lochiaOdourFoul));
        expectedObservations.add(new MRSObservation<Double>(registrationDate, TEMPERATURE.getName(), temperature));
        expectedObservations.add(new MRSObservation<Integer>(registrationDate, ANC_PNC_LOCATION.getName(), Integer.parseInt(location)));

        assertReflectionEquals(expectedObservations, mrsObservations, LENIENT_DATES, LENIENT_ORDER);
    }

    private ANCVisit createTestANCVisit() {
        String mrsFacilityId = "mrsFacilityId";
        MRSFacility mrsFacility = new MRSFacility(mrsFacilityId);
        Facility facility = new Facility(mrsFacility);
        Patient patient = new Patient(new MRSPatient("motechId", new MRSPerson(), mrsFacility));
        facility.mrsFacilityId(mrsFacilityId);
        MRSUser staff = new MRSUser();
        staff.id("staffId");

        return new ANCVisit().staff(staff).facility(facility).patient(patient).date(new Date()).serialNumber("4ds65")
                .visitNumber("4").estDeliveryDate(DateUtil.newDate(2012, 8, 8).toDate())
                .bpDiastolic(67).bpSystolic(10).weight(65.67d).comments("comments").ttdose("4").iptdose("5")
                .iptReactive(true).itnUse("Y").fht(4.3d).fhr(4).urineTestGlucosePositive("0").urineTestProteinPositive("1")
                .hemoglobin(13.8).vdrlReactive("N").vdrlTreatment(null).dewormer("Y").pmtct("Y").preTestCounseled("N")
                .hivTestResult("hiv").postTestCounseled("Y").pmtctTreament("Y").location("34").house("house").community("community")
                .referred("Y").maleInvolved(false).nextANCDate(DateUtil.newDate(2012, 2, 20).toDate());
    }

    private PNCMotherRequest createPNCMotherRequest(String comment, String community, DateTime date, Facility facility, MRSUser staff, String fht, String house, String location, Boolean lochiaAmountExcess, String lochiaColour,
                                                    Boolean lochiaOdourFoul, Boolean maleInvolved, Patient patient, double temperature, int visitNumber, String vitaminA) {
        final PNCMotherRequest pncMotherRequest = new PNCMotherRequest();
        pncMotherRequest.comments(comment).community(community).date(date).facility(facility).fht(fht).house(house).location(location).lochiaAmountExcess(lochiaAmountExcess).lochiaColour(lochiaColour)
                .lochiaOdourFoul(lochiaOdourFoul).staff(staff).maleInvolved(maleInvolved).referred(Boolean.TRUE).patient(patient)
                .temperature(temperature).visit(PNCMotherVisit.byVisitNumber(visitNumber)).vitaminA(vitaminA).ttDose("1");
        return pncMotherRequest;
    }

}
