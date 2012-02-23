package org.motechproject.ghana.national.factory;

import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_VISIT;

public class ChildVisitEncounterFactory extends BaseObservationFactory {

    public Encounter createEncounter(CWCVisit cwcVisit) {
        return new Encounter(cwcVisit.getPatient().getMrsPatient(), cwcVisit.getStaff(),
                cwcVisit.getFacility(), CWC_VISIT, cwcVisit.getDate(), createMRSObservations(cwcVisit));
    }

    Set<MRSObservation> createMRSObservations(CWCVisit cwcVisit) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = cwcVisit.getDate() == null ? DateUtil.today().toDate() : cwcVisit.getDate();

        setObservation(mrsObservations, registrationDate, SERIAL_NUMBER.getName(), cwcVisit.getSerialNumber());
        setObservation(mrsObservations, registrationDate, MALE_INVOLVEMENT.getName(), cwcVisit.getMaleInvolved());
        setObservation(mrsObservations, registrationDate, WEIGHT_KG.getName(), cwcVisit.getWeight());
        setObservation(mrsObservations, registrationDate, HEIGHT.getName(), cwcVisit.getHeight());
        setObservation(mrsObservations, registrationDate, MUAC.getName(), cwcVisit.getMuac());
        if (!cwcVisit.getCwcLocation().equals(Constants.NOT_APPLICABLE))
            setObservation(mrsObservations, registrationDate, CWC_LOCATION.getName(), toInteger(cwcVisit.getCwcLocation()));
        setObservation(mrsObservations, registrationDate, HOUSE.getName(), cwcVisit.getHouse());
        setObservation(mrsObservations, registrationDate, COMMUNITY.getName(), cwcVisit.getCommunity());
        setObservation(mrsObservations, registrationDate, COMMENTS.getName(), cwcVisit.getComments());
        setObservation(mrsObservations, registrationDate, PENTA.getName(), cwcVisit.getPentadose());
        setObservation(mrsObservations, registrationDate, OPV.getName(), cwcVisit.getOpvdose());
        setObservation(mrsObservations, registrationDate, IPTI.getName(), cwcVisit.getIptidose());

        for (String immunization : cwcVisit.getImmunizations()) {
            setObservation(mrsObservations, registrationDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(Concept.valueOf(immunization).getName()));
        }
        return mrsObservations;
    }
}
