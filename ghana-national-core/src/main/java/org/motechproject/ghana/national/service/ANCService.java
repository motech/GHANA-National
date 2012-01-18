package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
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
    @Autowired
    MobileMidwifeService mobileMidwifeService;

    public static final String ANCREGVISIT = "ANCREGVISIT";


    public MRSEncounter enroll(ANCVO ancVO) {
        MRSUser mrsUser = staffService.getUserByEmailIdOrMotechId(ancVO.getStaffId());
        MRSPatient mrsPatient = patientService.getPatientByMotechId(ancVO.getMotechPatientId()).getMrsPatient();
        MRSFacility mrsFacility = facilityService.getFacility(ancVO.getFacilityId()).mrsFacility();
        MRSPerson mrsPerson = mrsUser.getPerson();
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date observationDate = new Date();
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? observationDate : ancVO.getRegistrationDate();

        mrsObservations.add(new MRSObservation<Integer>(observationDate, Constants.CONCEPT_GRAVIDA, ancVO.getGravida()));
        mrsObservations.add(new MRSObservation<Double>(observationDate, Constants.CONCEPT_HEIGHT, ancVO.getHeight()));
        mrsObservations.add(new MRSObservation<Integer>(observationDate, Constants.CONCEPT_PARITY, ancVO.getParity()));
        mrsObservations.add(new MRSObservation<Date>(observationDate, Constants.CONCEPT_EDD, ancVO.getEstimatedDateOfDelivery()));
        mrsObservations.add(new MRSObservation<Boolean>(observationDate, Constants.CONCEPT_CONFINEMENT_CONFIRMED, ancVO.getDeliveryDateConfirmed()));
        mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_ANC_REG_NUM, ancVO.getSerialNumber()));

        if (ancVO.getLastIPT() != null && ancVO.getLastIPTDate() != null) {
            mrsObservations.add(new MRSObservation<Integer>(ancVO.getLastIPTDate(), Constants.CONCEPT_IPT, convertToInt(ancVO.getLastIPT())));
        }
        if (ancVO.getLastTT() != null && ancVO.getLastTTDate() != null) {
            mrsObservations.add(new MRSObservation<Integer>(ancVO.getLastTTDate(), Constants.CONCEPT_TT, convertToInt(ancVO.getLastTT())));
        }

        MRSEncounter mrsEncounter = new MRSEncounter(null, mrsPerson, mrsUser, mrsFacility, registrationDate, mrsPatient, mrsObservations, ANCREGVISIT);

        return allEncounters.save(mrsEncounter);

    }

    public MRSEncounter enrollWithMobileMidwife(ANCVO ancVO, MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        MRSEncounter mrsEncounter = enroll(ancVO);
        mobileMidwifeService.createOrUpdateEnrollment(mobileMidwifeEnrollment);
        return mrsEncounter;
    }

    public MRSEncounter getEncounter(String motechId) {
        return allEncounters.fetchLatest(motechId, ANCREGVISIT);
    }

    private Integer convertToInt(String vaccineValue) {
        return (vaccineValue != null) ? Integer.valueOf(vaccineValue) : null;
    }
}
