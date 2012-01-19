package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllEncounters;
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

    @Autowired
    AllEncounters allEncounters;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    public MRSEncounter enroll(CwcVO cwc, String encounterType) {
        MRSUser user = staffService.getUserByEmailIdOrMotechId(cwc.getStaffId());
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        String facilityId = cwc.getFacilityId();
        Date registrationDate = cwc.getRegistrationDate();
        Patient patient = patientService.getPatientByMotechId(cwc.getPatientMotechId());
        String patientMotechId = patient.getMrsPatient().getId();

        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        if (cwc.getCareHistories() != null) {
            if (cwc.getCareHistories().contains(CwcCareHistory.BCG)) {
                mrsObservations.add(new MRSObservation<Concept>(cwc.getBcgDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(Constants.CONCEPT_BCG)));
            }
            if (cwc.getCareHistories().contains(CwcCareHistory.VITA_A)) {
                mrsObservations.add(new MRSObservation<Concept>(cwc.getVitADate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(Constants.CONCEPT_VITA)));
            }
            if (cwc.getCareHistories().contains(CwcCareHistory.MEASLES)) {
                mrsObservations.add(new MRSObservation<Concept>(cwc.getMeaslesDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(Constants.CONCEPT_MEASLES)));
            }
            if (cwc.getCareHistories().contains(CwcCareHistory.YF)) {
                mrsObservations.add(new MRSObservation<Concept>(cwc.getYfDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMRSConceptAdaptor.getConceptByName(Constants.CONCEPT_YF)));
            }
            if (cwc.getCareHistories().contains(CwcCareHistory.PENTA)) {
                mrsObservations.add(new MRSObservation<Integer>(cwc.getLastPentaDate(), Constants.CONCEPT_PENTA, cwc.getLastPenta()));
            }
            if (cwc.getCareHistories().contains(CwcCareHistory.OPV)) {
                mrsObservations.add(new MRSObservation<Integer>(cwc.getLastOPVDate(), Constants.CONCEPT_OPV, cwc.getLastOPV()));
            }
            if (cwc.getCareHistories().contains(CwcCareHistory.IPTI)) {
                mrsObservations.add(new MRSObservation<Integer>(cwc.getLastIPTiDate(), Constants.CONCEPT_IPTI, cwc.getLastIPTi()));
            }
        }
        mrsObservations.add(new MRSObservation<String>(cwc.getRegistrationDate(), Constants.CONCEPT_CWC_REG_NUMBER, cwc.getSerialNumber()));

        MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                registrationDate, patientMotechId, mrsObservations, encounterType);
        return allEncounters.save(mrsEncounter);
    }

    public MRSEncounter enrollWithMobileMidwife(CwcVO cwcVO, MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        MRSEncounter mrsEncounter = enroll(cwcVO, Constants.ENCOUNTER_CWCREGVISIT);
        mobileMidwifeService.createOrUpdateEnrollment(mobileMidwifeEnrollment);
        return mrsEncounter;
    }

    public MRSEncounter getEncounter(String motechId, String encounterType) {
        return allEncounters.fetchLatest(motechId, encounterType);

    }
}
