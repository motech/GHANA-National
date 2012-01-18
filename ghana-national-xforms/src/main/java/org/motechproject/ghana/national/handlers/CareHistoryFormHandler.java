package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.openmrs.services.OpenMRSConceptAdaptor;
import org.motechproject.server.event.annotations.MotechListener;
import org.openmrs.Concept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class CareHistoryFormHandler implements FormPublishHandler {

    public static final String FORM_BEAN = "formBean";
    private final Logger log = LoggerFactory.getLogger(CareHistoryFormHandler.class);
    @Autowired
    private OpenMRSConceptAdaptor openMrsConceptAdaptor;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.careHistory")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        CareHistoryForm form = (CareHistoryForm) event.getParameters().get(FORM_BEAN);

        List<MRSObservation> observations = new ArrayList<MRSObservation>();
        if (form.getBcgDate() != null)
            observations.add(new MRSObservation<Concept>(form.getBcgDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMrsConceptAdaptor.getConceptByName(Constants.CONCEPT_BCG)));

        if (form.getYellowFeverDate() != null)
            observations.add(new MRSObservation<Concept>(form.getYellowFeverDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMrsConceptAdaptor.getConceptByName(Constants.CONCEPT_YF)));

        if (form.getMeaslesDate() != null)
            observations.add(new MRSObservation<Concept>(form.getMeaslesDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMrsConceptAdaptor.getConceptByName(Constants.CONCEPT_MEASLES)));

        if (form.getLastVitaminADate() != null)
            observations.add(new MRSObservation<Concept>(form.getLastVitaminADate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED, openMrsConceptAdaptor.getConceptByName(Constants.CONCEPT_VITA)));

        if (form.getLastIPT() != null && form.getLastIPTDate() != null)
            observations.add(new MRSObservation<Integer>(form.getLastIPTDate(), Constants.CONCEPT_IPT, Integer.valueOf(form.getLastIPT())));

        if (form.getLastIPTI() != null && form.getLastIPTIDate() != null)
            observations.add(new MRSObservation<Integer>(form.getLastIPTIDate(), Constants.CONCEPT_IPTI, Integer.valueOf(form.getLastIPTI())));

        if (form.getLastTT() != null && form.getLastTTDate() != null)
            observations.add(new MRSObservation<Integer>(form.getLastTTDate(), Constants.CONCEPT_TT, Integer.valueOf(form.getLastTT())));

        
    }
}
