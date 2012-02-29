package org.motechproject.ghana.national.factory;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.EncounterType;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PregnancyTerminationEncounterFactoryTest {
    @Test
    public void shouldCreateEncounterForPregnancyTermination() {
        PregnancyTerminationEncounterFactory factory = new PregnancyTerminationEncounterFactory();

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

        Encounter encounter = factory.createEncounter(request);

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
}
