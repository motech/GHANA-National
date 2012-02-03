package org.motechproject.ghana.national.web.helper;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.web.CWCController;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.mrs.model.*;
import org.openmrs.Concept;
import org.openmrs.ConceptName;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CwcFormMapperTest {

    @Test
    public void shouldSetViewAttributes() {
        final CwcFormMapper cwcFormMapper = new CwcFormMapper();
        final Map<String, Object> actual = cwcFormMapper.setViewAttributes();

        assertEquals(new LinkedHashMap<CwcCareHistory, String>() {{
            put(CwcCareHistory.BCG, CwcCareHistory.BCG.getDescription());
            put(CwcCareHistory.IPTI, CwcCareHistory.IPTI.getDescription());
            put(CwcCareHistory.MEASLES, CwcCareHistory.MEASLES.getDescription());
            put(CwcCareHistory.OPV, CwcCareHistory.OPV.getDescription());
            put(CwcCareHistory.PENTA, CwcCareHistory.PENTA.getDescription());
            put(CwcCareHistory.VITA_A, CwcCareHistory.VITA_A.getDescription());
            put(CwcCareHistory.YF, CwcCareHistory.YF.getDescription());
        }}, actual.get(Constants.CARE_HISTORIES));

        assertEquals(new LinkedHashMap<RegistrationToday, String>() {{
            put(RegistrationToday.TODAY, RegistrationToday.TODAY.getDescription());
            put(RegistrationToday.IN_PAST, RegistrationToday.IN_PAST.getDescription());
            put(RegistrationToday.IN_PAST_IN_OTHER_FACILITY, RegistrationToday.IN_PAST_IN_OTHER_FACILITY.getDescription());
        }}, actual.get(CWCController.REGISTRATION_OPTIONS));

        assertEquals(new LinkedHashMap<Integer, String>() {{
            put(1, Constants.IPTI_1);
            put(2, Constants.IPTI_2);
            put(3, Constants.IPTI_3);
        }}, actual.get(Constants.LAST_IPTI));

        assertEquals(new LinkedHashMap<Integer, String>() {{
            put(0, Constants.OPV_0);
            put(1, Constants.OPV_1);
            put(2, Constants.OPV_2);
            put(3, Constants.OPV_3);
        }}, actual.get(Constants.LAST_OPV));

        assertEquals(new LinkedHashMap<Integer, String>() {{
            put(1, Constants.PENTA_1);
            put(2, Constants.PENTA_2);
            put(3, Constants.PENTA_3);

        }}, actual.get(Constants.LAST_PENTA));
    }

    @Test
    public void shouldConvertEncounterToFormView() {
        final CwcFormMapper cwcFormMapper = new CwcFormMapper();

        String providerId = "121";
        String creatorId = "3232";
        String facilityId = "32";
        Date registrationDate = new Date();
        String patientId = "2343";
        final HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        Concept measlesValue = mock(Concept.class);
        Concept yfValue = mock(Concept.class);
        Concept bcgValue = mock(Concept.class);
        Concept vitaValue = mock(Concept.class);

        when(measlesValue.getName()).thenReturn(new ConceptName(Constants.CONCEPT_MEASLES, Locale.getDefault()));
        when(yfValue.getName()).thenReturn(new ConceptName(Constants.CONCEPT_YF, Locale.getDefault()));
        when(bcgValue.getName()).thenReturn(new ConceptName(Constants.CONCEPT_BCG, Locale.getDefault()));
        when(vitaValue.getName()).thenReturn(new ConceptName(Constants.CONCEPT_VITA, Locale.getDefault()));

        Date measlesDate = new Date();
        Date yfDate = new Date();
        Date bcgDate = new Date();
        Date vitaDate = new Date();
        Date iptiDate = new Date();
        Date opvDate = new Date();
        Date pentaDate = new Date();
        Double iptiValue = 0.0;
        String serialNum = "serial number";
        Double pentaValue = 2.0;
        Double opvValue = 1.0;
        String name = "name";
        String country = "country";
        String region = "region";
        String county = "county";
        String province = "province";
        observations.add(new MRSObservation(measlesDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, measlesValue));
        observations.add(new MRSObservation(yfDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, yfValue));
        observations.add(new MRSObservation(bcgDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, bcgValue));
        observations.add(new MRSObservation(vitaDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, vitaValue));
        observations.add(new MRSObservation(iptiDate, Constants.CONCEPT_IPTI, iptiValue));
        observations.add(new MRSObservation(pentaDate, Constants.CONCEPT_PENTA, pentaValue));
        observations.add(new MRSObservation(opvDate, Constants.CONCEPT_OPV, opvValue));
        observations.add(new MRSObservation(registrationDate, Constants.CONCEPT_CWC_REG_NUMBER, serialNum));
        MRSFacility facility = new MRSFacility(facilityId, name, country, region, county, province);
        MRSEncounter mrsEncounter = new MRSEncounter("1", new MRSPerson().id(providerId),
                new MRSUser().systemId(creatorId), facility, registrationDate, new MRSPatient(patientId, null, null), observations, "type");

        final CWCEnrollmentForm cwcEnrollmentForm = cwcFormMapper.mapEncounterToView(mrsEncounter);

        assertThat(cwcEnrollmentForm.getPatientMotechId(), is(equalTo(patientId)));
        assertThat(cwcEnrollmentForm.getStaffId(), is(equalTo(creatorId)));
        assertThat(cwcEnrollmentForm.getFacilityForm().getFacilityId(), is(equalTo(facility.getId())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getCountry(), is(equalTo(facility.getCountry())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getCountyDistrict(), is(equalTo(facility.getCountyDistrict())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getName(), is(equalTo(facility.getName())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getStateProvince(), is(equalTo(facility.getStateProvince())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getRegion(), is(equalTo(facility.getRegion())));

        assertThat(cwcEnrollmentForm.getMeaslesDate(), is(equalTo(measlesDate)));
        assertThat(cwcEnrollmentForm.getBcgDate(), is(equalTo(bcgDate)));
        assertThat(cwcEnrollmentForm.getYfDate(), is(equalTo(yfDate)));
        assertThat(cwcEnrollmentForm.getVitADate(), is(equalTo(vitaDate)));
        assertThat(cwcEnrollmentForm.getLastIPTiDate(), is(equalTo(iptiDate)));
        assertThat(cwcEnrollmentForm.getLastIPTi(), is(equalTo(iptiValue.intValue())));
        assertThat(cwcEnrollmentForm.getLastPentaDate(), is(equalTo(pentaDate)));
        assertThat(cwcEnrollmentForm.getLastPenta(), is(equalTo(pentaValue.intValue())));
        assertThat(cwcEnrollmentForm.getLastOPVDate(), is(equalTo(opvDate)));
        assertThat(cwcEnrollmentForm.getLastOPV(), is(equalTo(opvValue.intValue())));
        assertThat(cwcEnrollmentForm.getSerialNumber(), is(equalTo(serialNum)));
        assertThat(cwcEnrollmentForm.getAddHistory(), is(equalTo(true)));
    }

    @Test
    public void shouldConvertEncounterToFormViewAndSetAddHistoryToFalseIfNoObservationsAreFound() {
        final CwcFormMapper cwcFormMapper = new CwcFormMapper();

        String providerId = "121";
        String creatorId = "3232";
        String facilityId = "32";
        Date registrationDate = new Date();
        String patientId = "2343";
        final HashSet<MRSObservation> observations = new HashSet<MRSObservation>();

        String serialNum = "serial number";
        String name = "name";
        String country = "country";
        String region = "region";
        String county = "county";
        String province = "province";
        observations.add(new MRSObservation(registrationDate, Constants.CONCEPT_CWC_REG_NUMBER, serialNum));
        MRSFacility facility = new MRSFacility(facilityId, name, country, region, county, province);
        MRSEncounter mrsEncounter = new MRSEncounter("1", new MRSPerson().id(providerId),
                new MRSUser().systemId(creatorId), facility, registrationDate, new MRSPatient(patientId, null, null), observations, "type");

        final CWCEnrollmentForm cwcEnrollmentForm = cwcFormMapper.mapEncounterToView(mrsEncounter);

        assertThat(cwcEnrollmentForm.getPatientMotechId(), is(equalTo(patientId)));
        assertThat(cwcEnrollmentForm.getStaffId(), is(equalTo(creatorId)));
        assertThat(cwcEnrollmentForm.getFacilityForm().getFacilityId(), is(equalTo(facility.getId())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getCountry(), is(equalTo(facility.getCountry())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getCountyDistrict(), is(equalTo(facility.getCountyDistrict())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getName(), is(equalTo(facility.getName())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getStateProvince(), is(equalTo(facility.getStateProvince())));
        assertThat(cwcEnrollmentForm.getFacilityForm().getRegion(), is(equalTo(facility.getRegion())));

        assertThat(cwcEnrollmentForm.getSerialNumber(), is(equalTo(serialNum)));
        assertThat(cwcEnrollmentForm.getAddHistory(), is(equalTo(false)));
    }

}
