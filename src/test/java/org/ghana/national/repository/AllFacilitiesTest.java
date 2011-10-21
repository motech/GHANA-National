package org.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.Facility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public class AllFacilitiesTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("couchDbConnector")
    @Autowired
    protected CouchDbConnector dbConnector;

    @Mock
    MRSFacilityAdapter mockMrsFacilityAdapter;

    @Autowired
    private AllFacilities allFacilities;

    @Before
    public void init() {
        initMocks(this);
        ReflectionTestUtils.setField(allFacilities, "facilityAdapter", mockMrsFacilityAdapter);
    }

    @Test
    public void shouldSaveAFacility() throws Exception {
        final String facilityName = "name";
        final String phoneNumber = "0123456789";
        String additionalPhone1 = "12345";
        String additionalPhone2 = "321234";
        String additionalPhone3 = "33344";
        final org.motechproject.mrs.model.Facility mrsFacility = new org.motechproject.mrs.model.Facility(facilityName);
        Facility facility = new Facility(mrsFacility)
                .phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhone1).additionalPhoneNumber2(additionalPhone2)
                .additionalPhoneNumber3(additionalPhone3);
        final int initialSize = allFacilities.getAll().size();

        final String facilityId = "12";
        when(mockMrsFacilityAdapter.saveFacility(mrsFacility)).thenReturn(new org.motechproject.mrs.model.Facility(facilityId, facilityName,
                "country", "region", "district", "province"));

        allFacilities.add(facility);

        final List<Facility> facilities = allFacilities.getAll();
        final int actualSize = facilities.size();
        final Facility actualFacility = facilities.iterator().next();
        assertThat(actualSize, is(equalTo(initialSize + 1)));
        assertThat(actualFacility.phoneNumber(), is(equalTo(phoneNumber)));
        assertThat(actualFacility.additionalPhoneNumber1(), is(equalTo(additionalPhone1)));
        assertThat(actualFacility.additionalPhoneNumber2(), is(equalTo(additionalPhone2)));
        assertThat(actualFacility.additionalPhoneNumber3(), is(equalTo(additionalPhone3)));
        assertThat(actualFacility.mrsFacilityId(), is(equalTo(Integer.parseInt(facilityId))));
    }

    @Test
    public void shouldGetAllFacilitiesByName() {
        final String facilityName = "name";
        final String country = "Utopia";
        final String region = "Region 1";
        final String district = "District 1";
        final String province = "Province 1";
        final org.motechproject.mrs.model.Facility facility = new org.motechproject.mrs.model.Facility(
                facilityName, country, region, district, province);
        when(mockMrsFacilityAdapter.getFacilities(facilityName)).thenReturn(Arrays.asList(facility));
        final List<Facility> actualFacilities = allFacilities.facilitiesByName(facilityName);
        final Facility actualFacility = actualFacilities.iterator().next();
        assertThat(actualFacilities.size(), is(equalTo(1)));
        assertThat(actualFacility.name(), is(equalTo(facilityName)));
        assertThat(actualFacility.country(), is(equalTo(country)));
        assertThat(actualFacility.region(), is(equalTo(region)));
        assertThat(actualFacility.province(), is(equalTo(province)));
        assertThat(actualFacility.district(), is(equalTo(district)));
    }

    @Test
    public void shouldGetAllFacilities() {
        final String facilityName = "name";
        final String country = "Utopia";
        final String region = "Region 1";
        final String district = "District 1";
        final String province = "Province 1";
        final org.motechproject.mrs.model.Facility facility = new org.motechproject.mrs.model.Facility(
                facilityName, country, region, district, province);
        when(mockMrsFacilityAdapter.getFacilities()).thenReturn(Arrays.asList(facility));
        final List<Facility> actualFacilities = allFacilities.facilities();
        final Facility actualFacility = actualFacilities.iterator().next();
        assertThat(actualFacilities.size(), is(equalTo(1)));
        assertThat(actualFacility.name(), is(equalTo(facilityName)));
        assertThat(actualFacility.country(), is(equalTo(country)));
        assertThat(actualFacility.region(), is(equalTo(region)));
        assertThat(actualFacility.province(), is(equalTo(province)));
        assertThat(actualFacility.district(), is(equalTo(district)));
    }

    @After
    public void tearDown() {
        List<Facility> all = allFacilities.getAll();
        for (Facility facility : all)
            allFacilities.remove(facility);
    }
}
