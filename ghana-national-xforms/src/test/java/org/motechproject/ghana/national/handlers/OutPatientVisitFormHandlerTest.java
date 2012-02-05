package org.motechproject.ghana.national.handlers;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.services.OpenMRSConceptAdapter;
import org.motechproject.util.DateUtil;
import org.openmrs.Concept;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class OutPatientVisitFormHandlerTest {

    OutPatientVisitFormHandler handler;

    @Mock
    EncounterService mockEncounterService;

    @Mock
    MRSPatientAdapter mockMRSPatientAdapter;

    @Mock
    FacilityService mockFacilityService;

    @Mock
    OpenMRSConceptAdapter mockOpenMRSConceptAdapter;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new OutPatientVisitFormHandler();
        ReflectionTestUtils.setField(handler, "encounterService", mockEncounterService);
        ReflectionTestUtils.setField(handler, "patientAdapter", mockMRSPatientAdapter);
        ReflectionTestUtils.setField(handler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(handler, "openMRSConceptAdapter", mockOpenMRSConceptAdapter);
    }

    @Test
    public void shouldHandleEvent() {

        final OutPatientVisitForm form = new OutPatientVisitForm();
        String staffId = "staffId";
        String motechFacilityId = "motechFacilityId";
        String facilityId = "facilityId";
        Date visitDate = DateUtil.today().toDate();
        int secondDiagnosis = 65;
        boolean isNewCase = true;
        boolean isNewPatient = true;
        boolean isReferred = true;
        boolean isInsured = true;
        int diagnosis = 10;
        String motechId = "motechId";
        boolean rdtGiven = true;
        boolean rdtPositive = false;
        boolean actTreated = false;

        form.setNewCase(isNewCase);
        form.setStaffId(staffId);
        form.setFacilityId(motechFacilityId);
        form.setMotechId(motechId);
        form.setVisitDate(visitDate);
        String comments = "comments";
        form.setComments(comments);
        form.setDiagnosis(OutPatientVisitFormHandler.OTHER_DIAGNOSIS);
        form.setOtherDiagnosis(diagnosis);
        form.setSecondDiagnosis(secondDiagnosis);
        form.setNewPatient(isNewPatient);
        form.setReferred(isReferred);
        form.setInsured(isInsured);
        form.setRdtGiven(rdtGiven);
        form.setRdtPositive(rdtPositive);
        form.setActTreated(actTreated);

        MotechEvent motechEvent = new MotechEvent("form.validation.successful.NurseDataEntry.opvVisit", new HashMap<String, Object>() {{
            put(Constants.FORM_BEAN, form);
        }});

        MRSPatient mockMrsPatient = mock(MRSPatient.class);
        Facility mockFacility = mock(Facility.class);
        Concept mockConcept = mock(Concept.class);

        when(mockMRSPatientAdapter.getPatientByMotechId(motechId)).thenReturn(mockMrsPatient);
        when(mockFacilityService.getFacilityByMotechId(motechFacilityId)).thenReturn(mockFacility);
        when(mockFacility.mrsFacilityId()).thenReturn(facilityId);
        when(mockOpenMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NEGATIVE)).thenReturn(mockConcept);

        handler.handleFormEvent(motechEvent);

        ArgumentCaptor<Set> argumentCaptor = ArgumentCaptor.forClass(Set.class);

        verify(mockEncounterService).persistEncounter(eq(mockMrsPatient), eq(staffId), eq(facilityId),
                eq(Constants.ENCOUNTER_OUTPATIENTVISIT), eq(visitDate), argumentCaptor.capture());

        Set<MRSObservation> actualObservations = argumentCaptor.getValue();

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_NEW_CASE, isNewCase));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_INSURED, isInsured));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_NEW_PATIENT, isNewPatient));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_PRIMARY_DIAGNOSIS, diagnosis));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_SECONDARY_DIAGNOSIS, secondDiagnosis));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_REFERRED, isReferred));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_COMMENTS, comments));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_MALARIA_RAPID_TEST,
                mockOpenMRSConceptAdapter.getConceptByName(Constants.CONCEPT_NEGATIVE)));
        expectedObservations.add(new MRSObservation(visitDate, Constants.CONCEPT_ACT_TREATMENT, actTreated));

        assertReflectionEquals(actualObservations, expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }
}
