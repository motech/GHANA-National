package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

public class CareHistoryFormHandlerTest {
    private CareHistoryFormHandler careHistoryFormHandler;
    @Mock
    private AllEncounters mockAllEncounters;

    @Before
    public void setUp() {
        careHistoryFormHandler = new CareHistoryFormHandler();
    }

    @Test
    @Ignore
    public void shouldAddObservationForIPT() {
        Map<String, Object> parameter = new HashMap<String, Object>();
        Date now = DateUtil.now().toDate();
        parameter.put("formBean", careHistoryForm(now));
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.careHistory", parameter);

        careHistoryFormHandler.handleFormEvent(event);

        ArgumentCaptor<MRSEncounter> captor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(captor.capture());
        MRSEncounter mrsEncounter = captor.getValue();

        assertThat(mrsEncounter.getEncounterType(), is(equalTo("PATIENTHISTORY")));
        Set<MRSObservation> observations = mrsEncounter.getObservations();
        assertEquals(observations.size(), 9);

        assertThat(select(observations, having(on(MRSObservation.class).getConceptName(), is(Constants.CONCEPT_IPT))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getConceptName(), is(Constants.CONCEPT_TT))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getConceptName(), is(Constants.CONCEPT_PENTA))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getConceptName(), is(Constants.CONCEPT_IPTI))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getConceptName(), is(Constants.CONCEPT_OPV))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getValue(), is(Constants.CONCEPT_BCG))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getValue(), is(Constants.CONCEPT_MEASLES))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getValue(), is(Constants.CONCEPT_YF))).get(0).getDate(), is(now));
        assertThat(select(observations, having(on(MRSObservation.class).getValue(), is(Constants.CONCEPT_VITA))).get(0).getDate(), is(now));
    }

    private CareHistoryForm careHistoryForm(Date now) {
        CareHistoryForm careHistoryForm = new CareHistoryForm();
        careHistoryForm.setFacilityId("facilityId");
        careHistoryForm.setStaffId("staffId");
        careHistoryForm.setMotechId("motechId");
        careHistoryForm.setLastIPT("1");
        careHistoryForm.setLastIPTDate(now);
        careHistoryForm.setLastOPV("0");
        careHistoryForm.setLastOPVDate(now);
        careHistoryForm.setLastTT("1");
        careHistoryForm.setLastTTDate(now);
        careHistoryForm.setLastPenta("1");
        careHistoryForm.setLastPentaDate(now);
        careHistoryForm.setLastIPTI("1");
        careHistoryForm.setLastIPTIDate(now);
        careHistoryForm.setLastVitaminADate(now);
        careHistoryForm.setMeaslesDate(now);
        careHistoryForm.setYellowFeverDate(now);
        careHistoryForm.setBcgDate(now);
        return careHistoryForm;
    }
}
