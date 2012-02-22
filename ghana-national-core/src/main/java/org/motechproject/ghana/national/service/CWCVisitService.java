package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_VISIT;

@Service
public class CWCVisitService {

    @Autowired
    EncounterService encounterService;
    @Autowired
    PatientService patientService;

    public MRSEncounter registerCWCVisit(CWCVisit cwcVisit) {
        Patient patientByMotechId = patientService.getPatientByMotechId(cwcVisit.getMotechId());
        return encounterService.persistEncounter(patientByMotechId.getMrsPatient(), cwcVisit.getStaffId(),
                cwcVisit.getFacilityId(), CWC_VISIT.value(), cwcVisit.getDate(), createMRSObservations(cwcVisit));
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
            setObservation(mrsObservations, registrationDate, IMMUNIZATIONS_ORDERED.getName(), immunization);
        }
        return mrsObservations;
    }

    private Integer toInteger(String value) {
        return (value != null && !value.equals(Constants.NOT_APPLICABLE)) ? Integer.parseInt(value) : null;
    }

    private MRSConcept getConceptReactionResult(String reading) {
        if (reading == null) return null;
        return (reading.equals(Constants.OBSERVATION_YES)) ? new MRSConcept(REACTIVE.getName()) : new MRSConcept(NON_REACTIVE.getName());
    }

    private MRSConcept getConceptForTest(String reading) {
        if (reading == null) return null;
        MRSConcept concept = null;
        switch (Integer.valueOf(reading)) {
            case 0:
                concept = new MRSConcept(NEGATIVE.getName());
                break;
            case 1:
                concept = new MRSConcept(POSITIVE.getName());
                break;
            case 2:
                concept = new MRSConcept(TRACE.getName());
                break;
        }
        return concept;
    }

    public <T> void setObservation(HashSet<MRSObservation> mrsObservations, Date registrationDate, String conceptType, T value) {
        if (value != null)
            mrsObservations.add(new MRSObservation<T>(registrationDate, conceptType, value));
    }
}
