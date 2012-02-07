package org.motechproject.ghana.national.handlers;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.OutPatientVisitService;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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

    @Mock
    OutPatientVisitService mockOutPatientVisitService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new OutPatientVisitFormHandler();
        ReflectionTestUtils.setField(handler, "encounterService", mockEncounterService);
        ReflectionTestUtils.setField(handler, "patientAdapter", mockMRSPatientAdapter);
        ReflectionTestUtils.setField(handler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(handler, "openMRSConceptAdapter", mockOpenMRSConceptAdapter);
        ReflectionTestUtils.setField(handler, "outPatientVisitService", mockOutPatientVisitService);
    }

    @Test
    public void shouldPersistEncounterIfPatientIsNotAVisitor() {

        final OutPatientVisitForm form = new OutPatientVisitForm();
        String staffId = "staffId";
        String motechFacilityId = "motechFacilityId";
        String facilityId = "facilityId";
        Date visitDate = DateUtil.today().toDate();
        PatientType registrantType = PatientType.PREGNANT_MOTHER;
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
        form.setVisitor(false);
        form.setRegistrantType(registrantType);
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

    @Test
    public void shouldPersistRecordIfPatientIsVisitor(){
        final OutPatientVisitForm form = new OutPatientVisitForm();
        String staffId = "staffId";
        String motechFacilityId = "motechFacilityId";
        String facilityId = "facilityId";
        Date visitDate = DateUtil.today().toDate();
        Date birthDate = new Date(2008,12,12);
        PatientType registrantType = PatientType.PREGNANT_MOTHER;
        int secondDiagnosis = 65;
        boolean isNewCase = true;
        boolean isNewPatient = true;
        boolean isReferred = true;
        boolean isInsured = true;
        String nhis="N his";
        Date nHisExp=new Date(2012,12,12);
        int diagnosis = 10;
        String motechId = "motechId";
        boolean rdtGiven = true;
        boolean rdtPositive = false;
        boolean actTreated = false;
        String comments = "comments";

        form.setStaffId(staffId);
        form.setFacilityId(motechFacilityId);
        form.setMotechId(motechId);
        form.setVisitDate(visitDate);
        form.setDateOfBirth(birthDate);
        form.setRegistrantType(registrantType);
        form.setVisitor(true);
        form.setDiagnosis(OutPatientVisitFormHandler.OTHER_DIAGNOSIS);
        form.setOtherDiagnosis(diagnosis);
        form.setSecondDiagnosis(secondDiagnosis);
        form.setNewCase(isNewCase);
        form.setNewPatient(isNewPatient);
        form.setInsured(isInsured);
        form.setNhis(nhis);
        form.setNhisExpires(nHisExp);
        form.setRdtGiven(rdtGiven);
        form.setRdtPositive(rdtPositive);
        form.setActTreated(actTreated);
        form.setReferred(isReferred);
        form.setComments(comments);

        MotechEvent motechEvent = new MotechEvent("form.validation.successful.NurseDataEntry.opvVisit", new HashMap<String, Object>() {{
            put(Constants.FORM_BEAN, form);
        }});

        handler.handleFormEvent(motechEvent);

        OutPatientVisit expectedVisit=new OutPatientVisit();
        expectedVisit.setFacilityId(motechFacilityId).setStaffId(staffId).setRegistrantType(registrantType).setVisitDate(visitDate)
                .setDateOfBirth(birthDate).setInsured(isInsured).setNhis(nhis).setNhisExpires(form.getNhisExpires())
                .setDiagnosis(diagnosis).setSecondDiagnosis(secondDiagnosis).setActTreated(actTreated).setRdtGiven(rdtGiven).setRdtPositive(rdtPositive)
                 .setReferred(isReferred).setComments(comments);

        ArgumentCaptor<OutPatientVisit>captor=ArgumentCaptor.forClass(OutPatientVisit.class);
        verify(mockOutPatientVisitService).registerVisit(captor.capture());
        OutPatientVisit actualVisit=captor.getValue();

        assertThat(actualVisit.getDateOfBirth(),is(expectedVisit.getDateOfBirth()));
        assertThat(actualVisit.getStaffId(),is(expectedVisit.getStaffId()));
        assertThat(actualVisit.getFacilityId(),is(expectedVisit.getFacilityId()));
        assertThat(actualVisit.getInsured(),is(expectedVisit.getInsured()));
        assertThat(actualVisit.getNhis(),is(expectedVisit.getNhis()));
        assertThat(actualVisit.getNhisExpires(),is(expectedVisit.getNhisExpires()));
        assertThat(actualVisit.getRegistrantType(),is(expectedVisit.getRegistrantType()));
        assertThat(actualVisit.getActTreated(),is(expectedVisit.getActTreated()));
        assertThat(actualVisit.getNewCase(),is(expectedVisit.getNewCase()));
        assertThat(actualVisit.getNewPatient(),is(expectedVisit.getNewPatient()));
        assertThat(actualVisit.getRdtGiven(),is(expectedVisit.getRdtGiven()));
        assertThat(actualVisit.getRdtPositive(),is(expectedVisit.getRdtPositive()));
        assertThat(actualVisit.getReferred(),is(expectedVisit.getReferred()));
        assertThat(actualVisit.getComments(),is(expectedVisit.getComments()));

    }
}
