package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.mrs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

@Service
public class ANCService {

    @Autowired
    AllEncounters allEncounters;
    @Autowired
    StaffService staffService;
    @Autowired
    PatientService patientService;
    @Autowired
    FacilityService facilityService;

    public static final String ANCREGVISIT = "ANCREGVISIT";

    public static final String CONCEPT_GRAVIDA = "GRAVIDA";
    public static final String CONCEPT_HEIGHT = "HEIGHT";
    public static final String CONCEPT_PARITY = "PARITY";
    public static final String CONCEPT_EDD = "ESTIMATED DATE OF DELIVERY";
    public static final String CONCEPT_ANC_REG_NUM = "ANC REGISTRATION NUMBER";
    public static final String CONCEPT_IPT = "INTERMITTENT PREVENTATIVE TREATMENT DOSE";
    public static final String CONCEPT_TT = "TETANUS TOXOID DOSE";
    public static final String CONCEPT_CONFINEMENT_CONFIRMED = "DATE OF CONFINEMENT CONFIRMED";

    public MRSEncounter enroll(ANCVO ancVO) {
        MRSUser mrsUser = staffService.getUserByEmailIdOrMotechId(ancVO.getStaffId());
        MRSPatient mrsPatient = patientService.getPatientByMotechId(ancVO.getMotechPatientId()).getMrsPatient();
        MRSFacility mrsFacility = facilityService.getFacilityByMotechId(ancVO.getFacilityId()).mrsFacility();
        MRSPerson mrsPerson = mrsUser.getPerson();
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date observationDate = new Date();

        mrsObservations.add(new MRSObservation<Integer>(observationDate, CONCEPT_GRAVIDA, ancVO.getGravida()));
        mrsObservations.add(new MRSObservation<Double>(observationDate, CONCEPT_HEIGHT, ancVO.getHeight()));
        mrsObservations.add(new MRSObservation<Integer>(observationDate, CONCEPT_PARITY, ancVO.getParity()));
        mrsObservations.add(new MRSObservation<Date>(observationDate, CONCEPT_EDD, ancVO.getEstimatedDateOfDelivery()));
        mrsObservations.add(new MRSObservation<Boolean>(observationDate, CONCEPT_CONFINEMENT_CONFIRMED, ancVO.getDeliveryDateConfirmed()));
        mrsObservations.add(new MRSObservation<String>(observationDate, CONCEPT_ANC_REG_NUM, ancVO.getSerialNumber()));

        if (ancVO.getLastIPT() != null && ancVO.getLastIPTDate() != null) {
            mrsObservations.add(new MRSObservation<Integer>(ancVO.getLastIPTDate(), CONCEPT_IPT, convertToInt(ancVO.getLastIPT())));
        }
        if (ancVO.getLastTT() != null && ancVO.getLastTTDate() != null) {
            mrsObservations.add(new MRSObservation<Integer>(ancVO.getLastTTDate(), CONCEPT_TT, convertToInt(ancVO.getLastTT())));
        }

        MRSEncounter mrsEncounter = new MRSEncounter(null, mrsPerson, mrsUser, mrsFacility, ancVO.getRegistrationDate(), mrsPatient, mrsObservations, ANCREGVISIT);

        return allEncounters.save(mrsEncounter);

    }


    private Integer convertToInt(String vaccineValue) {
        return (vaccineValue != null) ? Integer.valueOf(vaccineValue) : null;
    }
}
