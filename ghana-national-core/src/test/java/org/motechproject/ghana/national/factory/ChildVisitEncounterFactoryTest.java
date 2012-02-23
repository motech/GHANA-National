package org.motechproject.ghana.national.factory;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
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
        expectedObservations.add(new MRSObservation<MRSConcept>(today, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(Concept.BCG.getName())));

        assertReflectionEquals(expectedObservations, mrsObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    private CWCVisit createTestCWCVisit(Date registrationDate, MRSUser staff, Facility facility, Patient patient) {
        CWCVisit cwcVisit = new CWCVisit();
        return cwcVisit.staff(staff).facility(facility).patient(patient).date(registrationDate).serialNumber("4ds65")
                .weight(65.67d).comments("comments").cwcLocation("34").house("house").community("community").maleInvolved(false).immunizations(asList("BCG"));
    }
}
