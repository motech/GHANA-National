package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.services.OpenMRSConceptAdaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class CWCServiceTest {

    CWCService cwcService;

    @Mock
    StaffService mockStaffService;

    @Mock
    PatientService mockPatientService;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    OpenMRSConceptAdaptor mockOpenMRSConceptAdaptor;

    @Mock
    MobileMidwifeService mockMockMidwifeService;

    @Before
    public void setUp() {
        cwcService = new CWCService();
        initMocks(this);
        ReflectionTestUtils.setField(cwcService, "staffService", mockStaffService);
        ReflectionTestUtils.setField(cwcService, "patientService", mockPatientService);
        ReflectionTestUtils.setField(cwcService, "allEncounters", mockAllEncounters);
        ReflectionTestUtils.setField(cwcService, "openMRSConceptAdaptor", mockOpenMRSConceptAdaptor);
        setField(cwcService, "mobileMidwifeService", mockMockMidwifeService);
    }

    @Test
    public void shouldEnrollCWCProgram() {
        final Date registartionDate = new Date(2011, 9, 1);
        final Date lastBCGDate = new Date(2011, 10, 1);
        final Date lastVitADate = new Date(2011, 11, 1);
        final Date lastMeaslesDate = new Date(2011, 9, 2);
        final Date lastYfDate = new Date(2011, 9, 3);
        final Date lastPentaDate = new Date(2011, 9, 4);
        final Date lastOPVDate = new Date(2011, 9, 5);
        final Date lastIPTiDate = new Date(2011, 9, 6);
        final String staffId = "456";
        final int lastIPTi = 1;
        final int lastPenta = 1;
        final String patientMotechId = "1234567";
        final int lastOPV = 0;
        final String facilityId = "3232";
        final String serialNumber = "wewew";
        CwcVO cwcVO = new CwcVO(staffId, facilityId, registartionDate, patientMotechId, lastBCGDate, lastVitADate,
                lastMeaslesDate, lastYfDate, lastPentaDate, lastPenta, lastOPVDate, lastOPV, lastIPTiDate, lastIPTi, serialNumber);

        final MRSUser mrsUser = new MRSUser();
        final String staffUserId = "12";
        mrsUser.id(staffUserId);
        final MRSPerson person = new MRSPerson();
        final String staffPersonId = "32";
        person.id(staffPersonId);
        mrsUser.person(person);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mrsUser);
        final String patientId = "24324";
        final Patient patient = new Patient(new MRSPatient(patientId));
        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(patient);

        cwcService.enroll(cwcVO, Constants.ENCOUNTER_CWCREGVISIT);

        ArgumentCaptor<MRSEncounter> captor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(captor.capture());
        MRSEncounter actualEncounter = captor.getValue();
        assertThat(actualEncounter.getProvider().getId(), is(equalTo(staffPersonId)));
        assertThat(actualEncounter.getCreator().getId(), is(equalTo(staffUserId)));
        assertThat(actualEncounter.getFacility().getId(), is(equalTo(facilityId)));
        assertThat(actualEncounter.getDate(), is(equalTo(registartionDate)));
        assertThat(actualEncounter.getPatient().getId(), is(equalTo(patientId)));
        assertThat(actualEncounter.getEncounterType(), is(equalTo(Constants.ENCOUNTER_CWCREGVISIT)));
        assertThat(actualEncounter.getObservations().size(), is(8));
        final HashSet<MRSObservation> expected = new HashSet<MRSObservation>() {{
            add(new MRSObservation(lastBCGDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastVitADate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastMeaslesDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastYfDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastPentaDate, Constants.CONCEPT_PENTA, lastPenta));
            add(new MRSObservation(registartionDate, Constants.CONCEPT_CWC_REG_NUMBER, serialNumber));
            add(new MRSObservation(lastOPVDate, Constants.CONCEPT_OPV, lastOPV));
            add(new MRSObservation(lastIPTiDate, Constants.CONCEPT_IPTI, lastIPTi));
        }};
        assertReflectionEquals(actualEncounter.getObservations(), expected, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldNotIncludeConceptsIfNotGiven() {
        final Date registartionDate = new Date(2011, 9, 1);
        final Date lastBCGDate = null;
        final Date lastVitADate = null;
        final Date lastMeaslesDate = null;
        final Date lastYfDate = null;
        final Date lastPentaDate = null;
        final Date lastOPVDate = null;
        final Date lastIPTiDate = null;
        final String staffId = "456";
        final Integer lastIPTi = null;
        final Integer lastPenta = null;
        final String patientMotechId = "1234567";
        final Integer lastOPV = null;
        final String facilityId = "3232";
        String serialNumber = "wewew";
        CwcVO cwcVO = new CwcVO(staffId, facilityId, registartionDate, patientMotechId, lastBCGDate, lastVitADate,
                lastMeaslesDate, lastYfDate, lastPentaDate, lastPenta, lastOPVDate, lastOPV, lastIPTiDate, lastIPTi, serialNumber);

        final MRSUser mrsUser = new MRSUser();
        final String staffUserId = "12";
        mrsUser.id(staffUserId);
        final MRSPerson person = new MRSPerson();
        final String staffPersonId = "32";
        person.id(staffPersonId);
        mrsUser.person(person);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mrsUser);
        final String patientId = "24324";
        final Patient patient = new Patient(new MRSPatient(patientId));
        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(patient);

        cwcService.enroll(cwcVO, Constants.ENCOUNTER_CWCREGVISIT);

        ArgumentCaptor<MRSEncounter> captor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(captor.capture());
        MRSEncounter actualEncounter = captor.getValue();
        assertThat(actualEncounter.getObservations().size(), is(1));
    }

    @Test
    public void shouldEnrollCWCWithMobileMidwife() {

        cwcService = spy(cwcService);
        MRSEncounter mrsEncounter = mock(MRSEncounter.class);
        doReturn(mrsEncounter).when(cwcService).enroll(Matchers.<CwcVO>any(), eq(Constants.ENCOUNTER_CWCREGVISIT));

        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment();
        MRSEncounter actualEncounter = cwcService.enrollWithMobileMidwife(mock(CwcVO.class), mobileMidwifeEnrollment);

        assertSame(mrsEncounter, actualEncounter);
        verify(mockMockMidwifeService).createOrUpdateEnrollment(mobileMidwifeEnrollment);
    }


}
