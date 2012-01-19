package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.services.OpenMRSConceptAdaptor;
import org.motechproject.openmrs.services.OpenMRSEncounterAdaptor;
import org.motechproject.util.DateUtil;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

@Service
public class CareService {
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

    public MRSEncounter getEncounter(String motechId, String encounterType) {
        return allEncounters.fetchLatest(motechId, encounterType);
    }

    public MRSEncounter enroll(CwcVO cwc) {
        return persistEncounter(cwc.getPatientMotechId(), cwc.getStaffId(), cwc.getFacilityId(), Constants.ENCOUNTER_CWCREGVISIT, cwc.getRegistrationDate(), prepareObservations(cwc));
    }

    public MRSEncounter enroll(CwcVO cwcVO, MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        MRSEncounter mrsEncounter = enroll(cwcVO);
        enroll(mobileMidwifeEnrollment);
        return mrsEncounter;
    }

    public MRSEncounter enroll(ANCVO ancVO) {
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? DateUtil.now().toDate(): ancVO.getRegistrationDate();
        return persistEncounter(ancVO.getPatientMotechId(), ancVO.getStaffId(), ancVO.getFacilityId(), Constants.ENCOUNTER_ANCREGVISIT, registrationDate, prepareObservations(ancVO));
    }

    public MRSEncounter enroll(ANCVO ancVO, MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        MRSEncounter mrsEncounter = enroll(ancVO);
        enroll(mobileMidwifeEnrollment);
        return mrsEncounter;
    }

    private void enroll(MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        mobileMidwifeService.createOrUpdateEnrollment(mobileMidwifeEnrollment);
    }

    private MRSEncounter persistEncounter(String patientMotechId, String staffId, String facilityId, String encounterType, Date registrationDate, HashSet<MRSObservation> mrsObservations) {
        MRSUser user = staffService.getUserByEmailIdOrMotechId(staffId);
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        Patient patient = patientService.getPatientByMotechId(patientMotechId);
        String patientId = patient.getMrsPatient().getId();
        MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                registrationDate, patientId, mrsObservations, encounterType);
        return allEncounters.save(mrsEncounter);
    }

    private HashSet<MRSObservation> prepareObservations(ANCVO ancVO) {
        Date observationDate = new Date();
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? observationDate : ancVO.getRegistrationDate();
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        if (ancVO.getGravida() != null)
            mrsObservations.add(new MRSObservation<Integer>(observationDate, Constants.CONCEPT_GRAVIDA, ancVO.getGravida()));
        if (ancVO.getHeight() != null)
            mrsObservations.add(new MRSObservation<Double>(observationDate, Constants.CONCEPT_HEIGHT, ancVO.getHeight()));
        if (ancVO.getParity() != null)
            mrsObservations.add(new MRSObservation<Integer>(observationDate, Constants.CONCEPT_PARITY, ancVO.getParity()));
        if (ancVO.getEstimatedDateOfDelivery() != null)
            mrsObservations.add(new MRSObservation<Date>(observationDate, Constants.CONCEPT_EDD, ancVO.getEstimatedDateOfDelivery()));
        if (ancVO.getDeliveryDateConfirmed() != null)
            mrsObservations.add(new MRSObservation<Boolean>(observationDate, Constants.CONCEPT_CONFINEMENT_CONFIRMED, ancVO.getDeliveryDateConfirmed()));
        if (ancVO.getSerialNumber() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_ANC_REG_NUM, ancVO.getSerialNumber()));
        if (ancVO.getCareHistory() != null) {
            if (ancVO.getCareHistory().contains(ANCCareHistory.IPT)) {
                mrsObservations.add(new MRSObservation<Integer>(ancVO.getLastIPTDate(), Constants.CONCEPT_IPT, Integer.parseInt(ancVO.getLastIPT())));
            }
            if (ancVO.getCareHistory().contains(ANCCareHistory.TT)) {
                mrsObservations.add(new MRSObservation<Integer>(ancVO.getLastTTDate(), Constants.CONCEPT_TT, Integer.parseInt(ancVO.getLastTT())));
            }
        }
        return mrsObservations;
    }

    private HashSet<MRSObservation> prepareObservations(CwcVO cwc) {
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
        return mrsObservations;
    }


}
