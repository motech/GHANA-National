package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.openmrs.services.OpenMRSConceptAdapter;
import org.motechproject.util.DateUtil;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ANCVisitService {

    @Autowired
    EncounterService encounterService;
    @Autowired
    PatientService patientService;
    @Autowired
    OpenMRSConceptAdapter openMRSConceptAdapter;

    public MRSEncounter registerANCVisit(ANCVisit ancVisit) {
        Patient patientByMotechId = patientService.getPatientByMotechId(ancVisit.getMotechId());
        Set<MRSObservation> mrsObservations = createMRSObservations(ancVisit);
        MRSEncounter mrsEncounter = encounterService.persistEncounter(patientByMotechId.getMrsPatient(), ancVisit.getStaffId(), ancVisit.getFacilityId(), Constants.ENCOUNTER_ANCVISIT, ancVisit.getDate(), mrsObservations);
        return mrsEncounter;
    }

    Set<MRSObservation> createMRSObservations(ANCVisit ancVisit) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = ancVisit.getDate() == null ? DateUtil.today().toDate() : ancVisit.getDate();

        if (ancVisit.getSerialNumber() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_SERIAL_NUMBER, ancVisit.getSerialNumber()));
        if (ancVisit.getVisitNumber() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_VISIT_NUMBER, ancVisit.getVisitNumber()));
        if (ancVisit.getMaleInvolved() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_MALE_INVOLVEMENT, ancVisit.getMaleInvolved()));
        if (ancVisit.getEstDeliveryDate() != null)
            mrsObservations.add(new MRSObservation<Date>(registrationDate, Constants.CONCEPT_EDD, ancVisit.getEstDeliveryDate()));
        if (ancVisit.getBpSystolic() != null)
            mrsObservations.add(new MRSObservation<Integer>(registrationDate, Constants.CONCEPT_SYSTOLIC_BLOOD_PRESSURE, ancVisit.getBpSystolic()));
        if (ancVisit.getBpDiastolic() != null)
            mrsObservations.add(new MRSObservation<Integer>(registrationDate, Constants.CONCEPT_DIASTOLIC_BLOOD_PRESSURE, ancVisit.getBpDiastolic()));
        if (ancVisit.getWeight() != null)
            mrsObservations.add(new MRSObservation<Double>(registrationDate, Constants.CONCEPT_WEIGHT_KG, ancVisit.getWeight()));
        if (ancVisit.getTtdose() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_TT, ancVisit.getTtdose()));
        if (ancVisit.getIptdose() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_IPT, ancVisit.getIptdose()));
        if (ancVisit.getItnUse() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE, ancVisit.getItnUse()));
        if (ancVisit.getFhr() != null)
            mrsObservations.add(new MRSObservation<Integer>(registrationDate, Constants.CONCEPT_FHR, ancVisit.getFhr()));
        if (ancVisit.getFht() != null)
            mrsObservations.add(new MRSObservation<Double>(registrationDate, Constants.CONCEPT_FHT, ancVisit.getFht()));

        String urineTestProtein = ancVisit.getUrineTestProteinPositive();
        if (urineTestProtein != null) {
            Concept urineProteinTestValueConcept = null;
            switch (Integer.valueOf(urineTestProtein)) {
                case 0:
                    urineProteinTestValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NEGATIVE);
                    break;
                case 1:
                    urineProteinTestValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_POSITIVE);
                    break;
                case 2:
                    urineProteinTestValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_TRACE);
                    break;
            }
            mrsObservations.add(new MRSObservation<Concept>(registrationDate, Constants.CONCEPT_URINE_PROTEIN_TEST, urineProteinTestValueConcept));
        }


        String urineTestGlucose = ancVisit.getUrineTestGlucosePositive();
        if (urineTestGlucose != null) {
            Concept urineGlucoseTestValueConcept = null;
            switch (Integer.valueOf(urineTestGlucose)) {
                case 0:
                    urineGlucoseTestValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NEGATIVE);
                    break;
                case 1:
                    urineGlucoseTestValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_POSITIVE);
                    break;
                case 2:
                    urineGlucoseTestValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_TRACE);
                    break;
            }
            mrsObservations.add(new MRSObservation<Concept>(registrationDate, Constants.CONCEPT_URINE_GLUCOSE_TEST, urineGlucoseTestValueConcept));
        }

        String iptReactive = ancVisit.getIptReactive();
        if (iptReactive != null) {
            Concept iptReactionValueConcept = null;
            if (iptReactive == "Y") {
                iptReactionValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_REACTIVE);
            } else {
                iptReactionValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NON_REACTIVE);
            }
            mrsObservations.add(new MRSObservation<Concept>(registrationDate, Constants.CONCEPT_IPT_REACTION, iptReactionValueConcept));
        }

        if (ancVisit.getHemoglobin() != null)
            mrsObservations.add(new MRSObservation<Double>(registrationDate, Constants.CONCEPT_HEMOGLOBIN, ancVisit.getHemoglobin()));

        String vdrlReactive = ancVisit.getVdrlReactive();
        if (vdrlReactive != null) {
            Concept vdrlValueConcept;
            if (vdrlReactive == "Y") {
                vdrlValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_REACTIVE);
            } else {
                vdrlValueConcept = openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NON_REACTIVE);
            }

            mrsObservations.add(new MRSObservation<Concept>(registrationDate, Constants.CONCEPT_VDRL, vdrlValueConcept));
        }

        if (ancVisit.getVdrlTreatment() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_VDRL_TREATMENT, ancVisit.getVdrlTreatment() == "Y" ? true : false));
        if (ancVisit.getDewormer() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_DEWORMER, ancVisit.getDewormer() == "Y" ? true : false));
        if (ancVisit.getPmtct() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_PMTCT, ancVisit.getPmtct() == "Y" ? true : false));
        if (ancVisit.getPmtctTreament() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_PMTCT_TREATMENT, ancVisit.getPmtctTreament() == "Y" ? true : false));
        if (ancVisit.getPreTestCounseled() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_HIV_PRE_TEST_COUNSELING, ancVisit.getPreTestCounseled() == "Y" ? true : false));
        if (ancVisit.getPostTestCounseled() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_HIV_POST_TEST_COUNSELING, ancVisit.getPostTestCounseled() == "Y" ? true : false));
        if (ancVisit.getHivTestResult() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_HIV_TEST_RESULT, ancVisit.getHivTestResult()));
        if (ancVisit.getLocation() != null)
            mrsObservations.add(new MRSObservation<Integer>(registrationDate, Constants.CONCEPT_ANC_PNC_LOCATION, Integer.parseInt(ancVisit.getLocation())));
        if (ancVisit.getHouse() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_HOUSE, ancVisit.getHouse()));
        if (ancVisit.getCommunity() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_COMMUNITY, ancVisit.getCommunity()));
        if (ancVisit.getReferred() != null)
            mrsObservations.add(new MRSObservation<Boolean>(registrationDate, Constants.CONCEPT_REFERRED, ancVisit.getReferred() == "Y" ? true : false));
        if (ancVisit.getComments() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_COMMENTS, ancVisit.getComments()));
        if (ancVisit.getNextANCDate() != null)
            mrsObservations.add(new MRSObservation<Date>(registrationDate, Constants.CONCEPT_NEXT_ANC_DATE, ancVisit.getNextANCDate()));


        return mrsObservations;
    }
}
