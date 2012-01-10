package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.services.OpenMRSConceptAdaptor;
import org.motechproject.openmrs.services.OpenMRSEncounterAdaptor;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

@Service
public class CWCService {

    @Autowired
    OpenMRSEncounterAdaptor openMRSEncounterAdaptor;

    @Autowired
    StaffService staffService;

    @Autowired
    PatientService patientService;

    @Autowired
    OpenMRSConceptAdaptor openMRSConceptAdaptor;

    static final String CWCREGVISIT = "CWCREGVISIT";

    static final String CONCEPT_OPV = "ORAL POLIO VACCINATION DOSE";
    static final String CONCEPT_PENTA = "PENTA VACCINATION DOSE";
    static final String CONCEPT_IPTI = "INTERMITTENT PREVENTATIVE TREATMENT INFANTS DOSE";
    static final String CONCEPT_MEASLES = "MEASLES VACCINATION";
    static final String CONCEPT_YF = "YELLOW FEVER VACCINATION";
    static final String CONCEPT_VITA = "VITAMIN A";
    static final String CONCEPT_BCG = "BACILLE CAMILE-GUERIN VACCINATION";
    static final String CONCEPT_IMMUNIZATIONS_ORDERED = "IMMUNIZATIONS ORDERED";

    public MRSEncounter enroll(CwcVO cwc) {
        MRSUser user = staffService.getUserById(cwc.getStaffId());
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        String facilityId = cwc.getFacilityId();
        Date registrationDate = cwc.getRegistrationDate();
        Patient patient = patientService.getPatientByMotechId(cwc.getPatientMotechId());
        String patientMotechId = patient.getMrsPatient().getId();

        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        if (cwc.getBcgDate() != null) {
            mrsObservations.add(new MRSObservation<Concept>(cwc.getBcgDate(), CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(CONCEPT_BCG)));
        }
        if (cwc.getVitADate() != null) {
            mrsObservations.add(new MRSObservation<Concept>(cwc.getVitADate(), CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(CONCEPT_VITA)));
        }
        if (cwc.getMeaslesDate() != null) {
            mrsObservations.add(new MRSObservation<Concept>(cwc.getMeaslesDate(), CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(CONCEPT_MEASLES)));
        }
        if (cwc.getYfDate() != null) {
            mrsObservations.add(new MRSObservation<Concept>(cwc.getYfDate(), CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(CONCEPT_YF)));
        }
        if (cwc.getLastPenta() != null) {
            mrsObservations.add(new MRSObservation<Integer>(cwc.getLastPentaDate(), CONCEPT_PENTA, cwc.getLastPenta()));
        }
        if (cwc.getLastOPV() != null) {
            mrsObservations.add(new MRSObservation<Integer>(cwc.getLastOPVDate(), CONCEPT_OPV, cwc.getLastOPV()));
        }
        if (cwc.getLastIPTi() != null) {
            mrsObservations.add(new MRSObservation<Integer>(cwc.getLastIPTiDate(), CONCEPT_IPTI, cwc.getLastIPTi()));
        }

        final MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                registrationDate, patientMotechId, mrsObservations, CWCREGVISIT);
        return openMRSEncounterAdaptor.createEncounter(mrsEncounter);
    }
}
