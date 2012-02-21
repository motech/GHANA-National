package org.motechproject.ghana.national.web.helper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.web.form.ANCEnrollmentForm;
import org.motechproject.mrs.model.*;

import java.util.Date;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.Concept.*;

public class ANCFormMapperTest {

    private ANCFormMapper ancFormMapper;

    @Before
    public void setUp() {
        ancFormMapper = new ANCFormMapper();
    }

    @Test
    public void shouldCreateANCEnrollmentFormFromMRSEncounter() {
        final Date observationDate = new Date();
        final Double gravida = 3.0;
        final Double height = 123.4;
        final Double parity = 3.0;
        final String serialNumber = "1A23WN3";

        String providerId = "121";
        String creatorId = "3232";
        String facilityId = "32";
        Date registrationDate = new Date();
        String patientId = "2343";
        String name = "name";
        String country = "country";
        String region = "region";
        String county = "county";
        String province = "province";

        final HashSet<MRSObservation> observations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(observationDate, GRAVIDA.getName(), gravida));
            add(new MRSObservation<Double>(observationDate, HEIGHT.getName(), height));
            add(new MRSObservation<Double>(observationDate, PARITY.getName(), parity));
            add(new MRSObservation<String>(observationDate, ANC_REG_NUM.getName(), serialNumber));
            add(new MRSObservation<Double>(observationDate, IPT.getName(), 2.0));
            add(new MRSObservation<Double>(observationDate, TT.getName(), 3.0));
        }};
        MRSFacility facility = new MRSFacility(facilityId, name, country, region, county, province);
        MRSEncounter mrsEncounter = new MRSEncounter("1", new MRSPerson().id(providerId),
                new MRSUser().systemId(creatorId), facility, registrationDate, new MRSPatient(patientId, null, null), observations, "type");
        ANCEnrollmentForm ancEnrollmentForm = ancFormMapper.convertMRSEncounterToView(mrsEncounter);

        assertThat(ancEnrollmentForm.getMotechPatientId(), is(equalTo(patientId)));
        assertThat(ancEnrollmentForm.getStaffId(), is(equalTo(creatorId)));
        assertThat(ancEnrollmentForm.getAddHistory(), is(true));
        assertThat(ancEnrollmentForm.getFacilityForm().getFacilityId(), is(equalTo(facility.getId())));
        assertThat(ancEnrollmentForm.getFacilityForm().getCountry(), is(equalTo(facility.getCountry())));
        assertThat(ancEnrollmentForm.getFacilityForm().getCountyDistrict(), is(equalTo(facility.getCountyDistrict())));
        assertThat(ancEnrollmentForm.getFacilityForm().getName(), is(equalTo(facility.getName())));
        assertThat(ancEnrollmentForm.getFacilityForm().getStateProvince(), is(equalTo(facility.getStateProvince())));
        assertThat(ancEnrollmentForm.getFacilityForm().getRegion(), is(equalTo(facility.getRegion())));

        assertThat(ancEnrollmentForm.getGravida(), is(equalTo(gravida.intValue())));
        assertThat(ancEnrollmentForm.getHeight(), is(equalTo(height)));
        assertThat(ancEnrollmentForm.getParity(), is(equalTo(parity.intValue())));


        assertThat(ancEnrollmentForm.getSerialNumber(), is(equalTo(serialNumber)));
        assertThat(ancEnrollmentForm.getLastIPT(), is(equalTo("2")));
        assertThat(ancEnrollmentForm.getLastTT(), is(equalTo("3")));

    }

    @Test
    public void shouldCreateANCEnrollmentFormFromMRSEncounterWhenNoObservationsFound() {
        final Date observationDate = new Date();
        final Double gravida = 3.0;
        final Double height = 123.4;
        final Double parity = 3.0;
        final String serialNumber = "1A23WN3";

        String providerId = "121";
        String creatorId = "3232";
        String facilityId = "32";
        Date registrationDate = new Date();
        String patientId = "2343";
        String name = "name";
        String country = "country";
        String region = "region";
        String county = "county";
        String province = "province";

        final HashSet<MRSObservation> observations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(observationDate, GRAVIDA.getName(), gravida));
            add(new MRSObservation<Double>(observationDate, HEIGHT.getName(), height));
            add(new MRSObservation<Double>(observationDate, PARITY.getName(), parity));
            add(new MRSObservation<String>(observationDate, ANC_REG_NUM.getName(), serialNumber));
        }};
        MRSFacility facility = new MRSFacility(facilityId, name, country, region, county, province);
        MRSEncounter mrsEncounter = new MRSEncounter("1", new MRSPerson().id(providerId),
                new MRSUser().systemId(creatorId), facility, registrationDate, new MRSPatient(patientId, null, null), observations, "type");
        ANCEnrollmentForm ancEnrollmentForm = ancFormMapper.convertMRSEncounterToView(mrsEncounter);

        assertThat(ancEnrollmentForm.getMotechPatientId(), is(equalTo(patientId)));
        assertThat(ancEnrollmentForm.getStaffId(), is(equalTo(creatorId)));
        assertThat(ancEnrollmentForm.getFacilityForm().getFacilityId(), is(equalTo(facility.getId())));
        assertThat(ancEnrollmentForm.getFacilityForm().getCountry(), is(equalTo(facility.getCountry())));
        assertThat(ancEnrollmentForm.getFacilityForm().getCountyDistrict(), is(equalTo(facility.getCountyDistrict())));
        assertThat(ancEnrollmentForm.getFacilityForm().getName(), is(equalTo(facility.getName())));
        assertThat(ancEnrollmentForm.getFacilityForm().getStateProvince(), is(equalTo(facility.getStateProvince())));
        assertThat(ancEnrollmentForm.getFacilityForm().getRegion(), is(equalTo(facility.getRegion())));

        assertThat(ancEnrollmentForm.getGravida(), is(equalTo(gravida.intValue())));
        assertThat(ancEnrollmentForm.getHeight(), is(equalTo(height)));
        assertThat(ancEnrollmentForm.getParity(), is(equalTo(parity.intValue())));
        assertThat(ancEnrollmentForm.getSerialNumber(), is(equalTo(serialNumber)));
        assertThat(ancEnrollmentForm.getAddHistory(), is(equalTo(false)));
    }
    
    @Test
    public void shouldPopulatePregnancyObservationInfoToView() {
        final Date observationDate = new Date();
        final Boolean deliveryDateConfirmed = true;
        final Date estimatedDateOfDelivery = new Date();
        final HashSet<MRSObservation> observations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Date>(observationDate, EDD.getName(), estimatedDateOfDelivery));
            add(new MRSObservation<Boolean>(observationDate, CONFINEMENT_CONFIRMED.getName(), deliveryDateConfirmed));
            add(new MRSObservation<Boolean>(observationDate, PREGNANCY_STATUS.getName(), true));
        }};

        ANCEnrollmentForm ancEnrollmentForm = new ANCEnrollmentForm();
        MRSEncounter mrsEncounter = new MRSEncounter(null, null, null, null, null, null, observations, null);
        ancFormMapper.populatePregnancyInfo(mrsEncounter, ancEnrollmentForm);
        
        assertThat(ancEnrollmentForm.getDeliveryDateConfirmed(), is(equalTo(deliveryDateConfirmed)));
        assertThat(ancEnrollmentForm.getEstimatedDateOfDelivery(), is(equalTo(estimatedDateOfDelivery)));
    }
}
