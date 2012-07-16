package org.motechproject.ghana.national.factory;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.service.request.PNCBabyRequest;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.*;
import org.motechproject.util.DateUtil;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ChildVisitEncounterFactoryTest {

    @Test
    public void shouldCreateEncounter() {
        Date today = DateUtil.now().toDate();

        MRSUser staff = mock(MRSUser.class);
        Facility facility = mock(Facility.class);
        Patient patient = mock(Patient.class);

        Set<MRSObservation> mrsObservations = new ChildVisitEncounterFactory().createMRSObservations(createTestCWCVisit(today, staff, facility, patient));
        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();

        expectedObservations.add(new MRSObservation<String>(today, SERIAL_NUMBER.getName(), "4ds65"));
        expectedObservations.add(new MRSObservation<Boolean>(today, MALE_INVOLVEMENT.getName(), false));
        expectedObservations.add(new MRSObservation<String>(today, COMMENTS.getName(), "comments"));
        expectedObservations.add(new MRSObservation<Integer>(today, CWC_LOCATION.getName(), 34));
        expectedObservations.add(new MRSObservation<Double>(today, WEIGHT_KG.getName(), 65.67d));
        expectedObservations.add(new MRSObservation<String>(today, HOUSE.getName(), "house"));
        expectedObservations.add(new MRSObservation<String>(today, COMMUNITY.getName(), "community"));
        expectedObservations.add(new MRSObservation<Integer>(today, ROTAVIRUS.getName(), 1));
        expectedObservations.add(new MRSObservation<MRSConcept>(today, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(Concept.BCG.getName())));
        expectedObservations.add(new MRSObservation<MRSConcept>(today, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(Concept.ROTAVIRUS.getName())));

        assertReflectionEquals(expectedObservations, mrsObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldCreateEncounterForPncBabyRequest() {
        MRSUser staff = new MRSUser();
        MRSFacility mrsFacility = new MRSFacility("1");
        Facility facility = new Facility(mrsFacility);
        MRSPatient mrsPatient = new MRSPatient("1");
        Patient patient = new Patient(mrsPatient);
        DateTime pncDate = DateTime.now();
        double weight = 65.67d;
        String comments = "comments";
        String location = "34";
        String house = "house";
        String community = "community";
        boolean maleInvolved = false;
        Boolean babyConditionGood = Boolean.TRUE;
        Boolean cordConditionNormal = Boolean.TRUE;
        boolean referred = false;
        boolean opv0 = false;
        boolean bcg = false;
        int respiration = 12;
        double temperature = 32.2d;
        int visitNumber = 1;
        PNCBabyRequest pncBabyRequest = new PNCBabyRequest().staff(staff).
                facility(facility).
                patient(patient).
                date(pncDate).
                weight(weight).
                comments(comments).
                location(location).
                house(house).
                community(community).
                maleInvolved(maleInvolved).
                babyConditionGood(babyConditionGood).
                cordConditionNormal(cordConditionNormal).
                referred(referred).
                bcg(bcg).opv0(opv0).
                respiration(respiration).
                temperature(temperature).
                visit(PNCChildVisit.byVisitNumber(visitNumber));

        HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>();
        expectedObservations.add(new MRSObservation<Integer>(pncDate.toDate(), VISIT_NUMBER.getName(), Integer.valueOf(visitNumber)));
        expectedObservations.add(new MRSObservation<Double>(pncDate.toDate(), TEMPERATURE.getName(), temperature));
        expectedObservations.add(new MRSObservation<Boolean>(pncDate.toDate(), MALE_INVOLVEMENT.getName(), maleInvolved));
        expectedObservations.add(new MRSObservation<Integer>(pncDate.toDate(), RESPIRATION.getName(), respiration));
        expectedObservations.add(new MRSObservation<Boolean>(pncDate.toDate(), CORD_CONDITION.getName(), cordConditionNormal));
        expectedObservations.add(new MRSObservation<Boolean>(pncDate.toDate(), BABY_CONDITION.getName(), babyConditionGood));
        expectedObservations.add(new MRSObservation<Double>(pncDate.toDate(), WEIGHT_KG.getName(), weight));
        expectedObservations.add(new MRSObservation<String>(pncDate.toDate(), BCG.getName(), "N"));
        expectedObservations.add(new MRSObservation<Boolean>(pncDate.toDate(), OPV.getName(), opv0));
        expectedObservations.add(new MRSObservation<Integer>(pncDate.toDate(), ANC_PNC_LOCATION.getName(), Integer.valueOf(location)));
        expectedObservations.add(new MRSObservation<String>(pncDate.toDate(), HOUSE.getName(), house));
        expectedObservations.add(new MRSObservation<String>(pncDate.toDate(), COMMUNITY.getName(), community));
        expectedObservations.add(new MRSObservation<Boolean>(pncDate.toDate(), REFERRED.getName(), referred));
        expectedObservations.add(new MRSObservation<String>(pncDate.toDate(), COMMENTS.getName(),comments));

        Encounter encounter = new ChildVisitEncounterFactory().createEncounter(pncBabyRequest);

        assertThat(encounter.getType(), is(EncounterType.PNC_CHILD_VISIT.value()));
        assertThat(encounter.getStaff(), is(staff));
        assertThat(encounter.getFacility(), is(mrsFacility));
        assertThat(encounter.getMrsPatient(), is(mrsPatient));
        assertThat(encounter.getDate(), is(pncDate.toDate()));

        assertReflectionEquals(expectedObservations, encounter.getObservations(), ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    private CWCVisit createTestCWCVisit(Date registrationDate, MRSUser staff, Facility facility, Patient patient) {
        CWCVisit cwcVisit = new CWCVisit();
        return cwcVisit.staff(staff).facility(facility).patient(patient).date(registrationDate).serialNumber("4ds65")
                .weight(65.67d).comments("comments").cwcLocation("34").house("house").community("community").maleInvolved(false).immunizations(asList("BCG","ROTAVIRUS")).rotavirusdose("1");
    }
}
