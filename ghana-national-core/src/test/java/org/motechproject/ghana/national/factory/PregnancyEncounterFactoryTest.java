package org.motechproject.ghana.national.factory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.service.request.DeliveredChildRequest;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.*;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PregnancyEncounterFactoryTest {
    PregnancyEncounterFactory factory;


    @Before
    public void setUp() {
        this.factory = new PregnancyEncounterFactory();
    }

    @Test
    public void shouldCreateEncounterForPregnancyTermination() {

        PregnancyTerminationRequest request = new PregnancyTerminationRequest();
        MRSFacility mrsFacility = new MRSFacility("122");
        Facility facility = new Facility(mrsFacility);
        request.setFacility(facility);
        MRSPatient mrsPatient = new MRSPatient("12");
        Patient patient = new Patient(mrsPatient);
        request.setPatient(patient);
        MRSUser staff = new MRSUser();
        request.setStaff(staff);
        request.setDead(Boolean.FALSE);
        request.setReferred(Boolean.FALSE);
        request.setTerminationProcedure("1");
        request.setTerminationType("2");
        request.setComments("Patient lost lot of blood");
        request.addComplication("1");
        request.addComplication("2");
        request.setPostAbortionFPAccepted(Boolean.TRUE);
        request.setPostAbortionFPCounselling(Boolean.FALSE);
        final Date terminationDate = new Date();
        request.setTerminationDate(terminationDate);

        Encounter encounter = factory.createTerminationEncounter(request);

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Boolean>(terminationDate, PREGNANCY_STATUS.getName(), false));
            add(new MRSObservation<Integer>(terminationDate, TERMINATION_TYPE.getName(), 2));
            add(new MRSObservation<Integer>(terminationDate, TERMINATION_PROCEDURE.getName(), 1));
            add(new MRSObservation<Integer>(terminationDate, TERMINATION_COMPLICATION.getName(), 1));
            add(new MRSObservation<Integer>(terminationDate, TERMINATION_COMPLICATION.getName(), 2));
            add(new MRSObservation<Boolean>(terminationDate, MATERNAL_DEATH.getName(), false));
            add(new MRSObservation<Boolean>(terminationDate, REFERRED.getName(), false));
            add(new MRSObservation<String>(terminationDate, COMMENTS.getName(), "Patient lost lot of blood"));
            add(new MRSObservation<Boolean>(terminationDate, POST_ABORTION_FP_COUNSELING.getName(), Boolean.FALSE));
            add(new MRSObservation<Boolean>(terminationDate, POST_ABORTION_FP_ACCEPTED.getName(), Boolean.TRUE));
        }};

        assertEquals(EncounterType.PREG_TERM_VISIT.value(), encounter.getType());
        assertEquals(mrsFacility, encounter.getFacility());
        assertEquals(mrsPatient, encounter.getMrsPatient());
        assertEquals(staff, encounter.getStaff());
        assertReflectionEquals(expectedObservations, encounter.getObservations(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldCreateEncounterForDelivery() {
        MRSFacility mrsFacility = new MRSFacility("122");
        Facility facility = new Facility(mrsFacility);
        String motechId = "motech-id";
        MRSPatient mrsPatient = new MRSPatient("12", motechId, new MRSPerson(), mrsFacility);
        Patient patient = new Patient(mrsPatient);
        MRSUser staff = new MRSUser();
        final DateTime deliveryTime = DateTime.now();
        final Date deliveryDate = deliveryTime.toDate();

        final PregnancyDeliveryRequest pregnancyDeliveryRequest = new PregnancyDeliveryRequest()
                .staff(staff)
                .patient(patient)
                .facility(facility)
                .deliveryDateTime(deliveryTime)
                .childDeliveryMode(ChildDeliveryMode.C_SECTION)
                .childDeliveryOutcome(ChildDeliveryOutcome.TRIPLETS)
                .maleInvolved(Boolean.FALSE)
                .childDeliveryLocation(ChildDeliveryLocation.GOVERNMENT_HOSPITAL)
                .childDeliveredBy(ChildDeliveredBy.CHO_OR_CHN)
                .deliveryComplications(DeliveryComplications.OTHER)
                .vvf(VVF.REFERRED)
                .maternalDeath(Boolean.FALSE)
                .comments("comments");
        final DeliveredChildRequest deliveredChildRequest1 = new DeliveredChildRequest();
        deliveredChildRequest1.childBirthOutcome(BirthOutcome.ALIVE);
        pregnancyDeliveryRequest.addDeliveredChildRequest(deliveredChildRequest1);

        final DeliveredChildRequest deliveredChildRequest2 = new DeliveredChildRequest();
        deliveredChildRequest2.childBirthOutcome(BirthOutcome.FRESH_STILL_BIRTH);
        pregnancyDeliveryRequest.addDeliveredChildRequest(deliveredChildRequest2);

        final DeliveredChildRequest deliveredChildRequest3 = new DeliveredChildRequest();
        deliveredChildRequest3.childBirthOutcome(BirthOutcome.MACERATED_STILL_BIRTH);
        pregnancyDeliveryRequest.addDeliveredChildRequest(deliveredChildRequest3);

        final MRSObservation activePregnancyObservation = new MRSObservation(new Date(), "PREG", "Value");
        Encounter encounter = factory.createDeliveryEncounter(pregnancyDeliveryRequest, activePregnancyObservation);

        MRSObservation<Boolean> pregnancyStatusObservation = new MRSObservation<Boolean>(deliveryDate, PREGNANCY_STATUS.getName(), Boolean.FALSE);
        activePregnancyObservation.addDependantObservation(pregnancyStatusObservation);

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(deliveryDate, DELIVERY_MODE.getName(), Integer.parseInt(pregnancyDeliveryRequest.getChildDeliveryMode().getValue())));
            add(new MRSObservation<Integer>(deliveryDate, DELIVERY_OUTCOME.getName(), Integer.parseInt(pregnancyDeliveryRequest.getChildDeliveryOutcome().getValue())));
            add(new MRSObservation<Boolean>(deliveryDate, MALE_INVOLVEMENT.getName(), pregnancyDeliveryRequest.getMaleInvolved()));
            add(new MRSObservation<Integer>(deliveryDate, DELIVERY_LOCATION.getName(), Integer.parseInt(pregnancyDeliveryRequest.getChildDeliveryLocation().getValue())));
            add(new MRSObservation<Integer>(deliveryDate, DELIVERED_BY.getName(), Integer.parseInt(pregnancyDeliveryRequest.getChildDeliveredBy().getValue())));
            add(new MRSObservation<Integer>(deliveryDate, DELIVERY_COMPLICATION.getName(), Integer.parseInt(pregnancyDeliveryRequest.getDeliveryComplications().getValue())));
            add(new MRSObservation<Integer>(deliveryDate, VVF_REPAIR.getName(), Integer.parseInt(pregnancyDeliveryRequest.getVvf().getValue())));
            add(new MRSObservation<Boolean>(deliveryDate, MATERNAL_DEATH.getName(), pregnancyDeliveryRequest.getMaternalDeath()));
            add(new MRSObservation<String>(deliveryDate, COMMENTS.getName(), pregnancyDeliveryRequest.getComments()));
            add(activePregnancyObservation);
            add(new MRSObservation<String>(deliveryDate, BIRTH_OUTCOME.getName(), deliveredChildRequest1.getChildBirthOutcome().getValue()));
            add(new MRSObservation<String>(deliveryDate, BIRTH_OUTCOME.getName(), deliveredChildRequest2.getChildBirthOutcome().getValue()));
            add(new MRSObservation<String>(deliveryDate, BIRTH_OUTCOME.getName(), deliveredChildRequest3.getChildBirthOutcome().getValue()));
        }};

        assertEquals(EncounterType.PREG_DEL_VISIT.value(), encounter.getType());
        assertEquals(mrsFacility, encounter.getFacility());
        assertEquals(mrsPatient, encounter.getMrsPatient());
        assertEquals(staff, encounter.getStaff());
        assertReflectionEquals(expectedObservations, encounter.getObservations(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldCreateEncounterForBirth() {
        MRSFacility mrsFacility = new MRSFacility("122");
        Facility facility = new Facility(mrsFacility);
        MRSPatient mrsPatient = new MRSPatient("12");
        Patient patient = new Patient(mrsPatient);
        MRSUser staff = new MRSUser();
        final Date birthDate = new Date();
        final String childWeight = "1.2";

        final DeliveredChildRequest childRequest = new DeliveredChildRequest();
        childRequest.childBirthOutcome(BirthOutcome.ALIVE);
        childRequest.childWeight(childWeight);

        Encounter encounter = factory.createBirthEncounter(childRequest, mrsPatient, staff, facility, birthDate);

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(birthDate, WEIGHT_KG.getName(), Double.parseDouble(childWeight)));
        }};

        assertEquals(EncounterType.BIRTH_VISIT.value(), encounter.getType());
        assertEquals(mrsFacility, encounter.getFacility());
        assertEquals(mrsPatient, encounter.getMrsPatient());
        assertEquals(staff, encounter.getStaff());
        assertReflectionEquals(expectedObservations, encounter.getObservations(), ReflectionComparatorMode.LENIENT_ORDER);
    }
}
