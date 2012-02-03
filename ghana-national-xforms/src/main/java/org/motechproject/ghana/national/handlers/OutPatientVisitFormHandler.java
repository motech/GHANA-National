package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OutPatientVisitFormHandler implements FormPublishHandler {


    static final String ENCOUNTER_TYPE = "OUTPATIENTVISIT";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EncounterService encounterService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    private MRSPatientAdaptor patientAdaptor;
    public static int OTHER_DIAGNOSIS = 78;


    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.opvVisit")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent motechEvent) {
        OutPatientVisitForm formBean = null;
        try {
            formBean = (OutPatientVisitForm) motechEvent.getParameters().get(Constants.FORM_BEAN);
            MRSPatient mrsPatient = patientAdaptor.getPatientByMotechId(formBean.getMotechId());
            Set<MRSObservation> observationList = new HashSet<MRSObservation>();

            if (formBean.getInsured() != null) {
                MRSObservation insuredObs = new MRSObservation<Boolean>(formBean.getVisitDate(), Constants.CONCEPT_INSURED, formBean.getInsured());
                observationList.add(insuredObs);
            }
            if (formBean.getNewCase() != null) {
                MRSObservation newCaseObs = new MRSObservation<Boolean>(formBean.getVisitDate(), Constants.CONCEPT_NEW_CASE, formBean.getNewCase());
                observationList.add(newCaseObs);
            }
            if (formBean.getNewPatient() != null) {
                MRSObservation newPatientObs = new MRSObservation<Boolean>(formBean.getVisitDate(), Constants.CONCEPT_NEW_PATIENT, formBean.getNewPatient());
                observationList.add(newPatientObs);
            }
            if (formBean.getDiagnosis() != null) {
                int obsVal = formBean.getDiagnosis() == OTHER_DIAGNOSIS ? formBean.getOtherDiagnosis() : formBean.getDiagnosis();
                MRSObservation diagnosisObs = new MRSObservation<Integer>(formBean.getVisitDate(), Constants.CONCEPT_PRIMARY_DIAGNOSIS, obsVal);
                observationList.add(diagnosisObs);

            }
            if (formBean.getSecondDiagnosis() != null) {
                int obsVal = formBean.getSecondDiagnosis() == OTHER_DIAGNOSIS ? formBean.getOtherSecondaryDiagnosis() : formBean.getSecondDiagnosis();
                MRSObservation secondDiagnosisObs = new MRSObservation<Integer>(formBean.getVisitDate(), Constants.CONCEPT_SECONDARY_DIAGNOSIS, obsVal);
                observationList.add(secondDiagnosisObs);

            }
            if (formBean.getReferred() != null) {
                MRSObservation referredObs = new MRSObservation<Boolean>(formBean.getVisitDate(), Constants.CONCEPT_REFERRED, formBean.getReferred());
                observationList.add(referredObs);

            }
            if (formBean.getComments() != null) {
                MRSObservation commentsObs = new MRSObservation<String>(formBean.getVisitDate(), Constants.CONCEPT_COMMENTS, formBean.getComments());
                observationList.add(commentsObs);

            }
            Facility facility = facilityService.getFacilityByMotechId(formBean.getFacilityId());
            encounterService.persistEncounter(mrsPatient, formBean.getStaffId(), facility.mrsFacilityId(), ENCOUNTER_TYPE, formBean.getVisitDate(), observationList);

        } catch (Exception e) {
            log.error("Exception occured in saving Delivery Notification details for: " + formBean.getMotechId(), e);

        }

    }
}
