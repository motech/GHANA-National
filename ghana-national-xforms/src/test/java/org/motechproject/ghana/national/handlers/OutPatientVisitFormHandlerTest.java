package org.motechproject.ghana.national.handlers;


import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.OutPatientVisitService;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.OUTPATIENT_VISIT;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class OutPatientVisitFormHandlerTest {

    OutPatientVisitFormHandler handler;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    MRSPatientAdapter mockMRSPatientAdapter;

    @Mock
    FacilityService mockFacilityService;

    @Mock
    OutPatientVisitService mockOutPatientVisitService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new OutPatientVisitFormHandler();
        ReflectionTestUtils.setField(handler, "allEncounters", mockAllEncounters);
        ReflectionTestUtils.setField(handler, "patientAdapter", mockMRSPatientAdapter);
        ReflectionTestUtils.setField(handler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(handler, "outPatientVisitService", mockOutPatientVisitService);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(mockMRSPatientAdapter).getPatientByMotechId(anyString());
        try {
            handler.handleFormEvent(new OutPatientVisitForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            MatcherAssert.assertThat(e.getMessage(), is("Exception occurred while processing Outpatient Visit form"));
        }
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

        MRSPatient mockMrsPatient = mock(MRSPatient.class);
        Facility mockFacility = mock(Facility.class);

        when(mockMRSPatientAdapter.getPatientByMotechId(motechId)).thenReturn(mockMrsPatient);
        when(mockFacilityService.getFacilityByMotechId(motechFacilityId)).thenReturn(mockFacility);
        when(mockFacility.mrsFacilityId()).thenReturn(facilityId);

        handler.handleFormEvent(form);

        ArgumentCaptor<Set> argumentCaptor = ArgumentCaptor.forClass(Set.class);

        verify(mockAllEncounters).persistEncounter(eq(mockMrsPatient), eq(staffId), eq(facilityId),
                eq(OUTPATIENT_VISIT.value()), eq(visitDate), argumentCaptor.capture());

        Set<MRSObservation> actualObservations = argumentCaptor.getValue();

        Set<MRSObservation> expectedObservations = new HashSet<MRSObservation>();
        expectedObservations.add(new MRSObservation<Boolean>(visitDate, NEW_CASE.getName(), isNewCase));
        expectedObservations.add(new MRSObservation<Boolean>(visitDate, NEW_PATIENT.getName(), isNewPatient));
        expectedObservations.add(new MRSObservation<Integer>(visitDate, PRIMARY_DIAGNOSIS.getName(), diagnosis));
        expectedObservations.add(new MRSObservation<Integer>(visitDate, SECONDARY_DIAGNOSIS.getName(), secondDiagnosis));
        expectedObservations.add(new MRSObservation<Boolean>(visitDate, REFERRED.getName(), isReferred));
        expectedObservations.add(new MRSObservation<String>(visitDate, COMMENTS.getName(), comments));
        expectedObservations.add(new MRSObservation<MRSConcept>(visitDate, MALARIA_RAPID_TEST.getName(),
                new MRSConcept(NEGATIVE.getName())));
        expectedObservations.add(new MRSObservation<Boolean>(visitDate, ACT_TREATMENT.getName(), actTreated));

        assertReflectionEquals(actualObservations, expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldPersistRecordIfPatientIsVisitor() {
        final OutPatientVisitForm form = new OutPatientVisitForm();
        String staffId = "staffId";
        String motechFacilityId = "motechFacilityId";
        Date visitDate = DateUtil.today().toDate();
        Date birthDate = new Date(2008, 12, 12);
        PatientType registrantType = PatientType.PREGNANT_MOTHER;
        int secondDiagnosis = 65;
        boolean isNewCase = true;
        boolean isNewPatient = true;
        boolean isReferred = true;
        boolean isInsured = true;
        String nhis = "N his";
        Date nHisExp = new Date(2012, 12, 12);
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

        handler.handleFormEvent(form);

        OutPatientVisit expectedVisit = new OutPatientVisit();
        expectedVisit.setFacilityId(motechFacilityId).setStaffId(staffId).setRegistrantType(registrantType).setVisitDate(visitDate)
                .setDateOfBirth(birthDate).setInsured(isInsured).setNhis(nhis).setNhisExpires(form.getNhisExpires())
                .setDiagnosis(diagnosis).setSecondDiagnosis(secondDiagnosis).setActTreated(actTreated).setRdtGiven(rdtGiven).setRdtPositive(rdtPositive)
                .setReferred(isReferred).setComments(comments).setNewCase(form.getNewCase()).setNewPatient(form.getNewPatient());

        ArgumentCaptor<OutPatientVisit> captor = ArgumentCaptor.forClass(OutPatientVisit.class);
        verify(mockOutPatientVisitService).registerVisit(captor.capture());
        OutPatientVisit actualVisit = captor.getValue();

        assertThat(actualVisit.getDateOfBirth(), is(expectedVisit.getDateOfBirth()));
        assertThat(actualVisit.getStaffId(), is(expectedVisit.getStaffId()));
        assertThat(actualVisit.getFacilityId(), is(expectedVisit.getFacilityId()));
        assertThat(actualVisit.getInsured(), is(expectedVisit.getInsured()));
        assertThat(actualVisit.getNhis(), is(expectedVisit.getNhis()));
        assertThat(actualVisit.getNhisExpires(), is(expectedVisit.getNhisExpires()));
        assertThat(actualVisit.getRegistrantType(), is(expectedVisit.getRegistrantType()));
        assertThat(actualVisit.getActTreated(), is(expectedVisit.getActTreated()));
        assertThat(actualVisit.getNewCase(), is(expectedVisit.getNewCase()));
        assertThat(actualVisit.getNewPatient(), is(expectedVisit.getNewPatient()));
        assertThat(actualVisit.getRdtGiven(), is(expectedVisit.getRdtGiven()));
        assertThat(actualVisit.getRdtPositive(), is(expectedVisit.getRdtPositive()));
        assertThat(actualVisit.getReferred(), is(expectedVisit.getReferred()));
        assertThat(actualVisit.getComments(), is(expectedVisit.getComments()));

    }
}
