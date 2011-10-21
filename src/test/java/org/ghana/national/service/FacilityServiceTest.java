package org.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.ghana.national.domain.Facility;
import org.ghana.national.exception.FacilityAlreadyFoundException;
import org.ghana.national.repository.AllFacilities;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacilityServiceTest {

    FacilityService facilityService;
    @Mock
    private AllFacilities mockAllFacilities;

    @Before
    public void init() {
        initMocks(this);
        facilityService = new FacilityService(mockAllFacilities);
        ReflectionTestUtils.setField(facilityService, "allFacilities", mockAllFacilities);
    }

    @Test
    public void shouldCreateAFacility() throws FacilityAlreadyFoundException {
        String facilityName = "name";
        String country = "country";
        String region = "region";
        String district = "district";
        String province = "province";
        String phoneNumber = "1";
        String additionalPhoneNumber1 = "2";
        String additionalPhoneNumber2 = "3";
        String additionalPhoneNumber3 = "4";
        when(mockAllFacilities.facilitiesByName(facilityName)).thenReturn(Collections.<Facility>emptyList());
        facilityService.create(facilityName, country, region, district, province, phoneNumber, additionalPhoneNumber1, additionalPhoneNumber2, additionalPhoneNumber3);
        final ArgumentCaptor<Facility> captor = ArgumentCaptor.forClass(Facility.class);
        verify(mockAllFacilities).add(captor.capture());

        final Facility savedFacility = captor.getValue();
        assertThat(savedFacility.name(), is(equalTo(facilityName)));
        assertThat(savedFacility.region(), is(equalTo(region)));
        assertThat(savedFacility.district(), is(equalTo(district)));
        assertThat(savedFacility.province(), is(equalTo(province)));
        assertThat(savedFacility.country(), is(equalTo(country)));
        assertThat(savedFacility.phoneNumber(), is(equalTo(phoneNumber)));
        assertThat(savedFacility.additionalPhoneNumber1(), is(equalTo(additionalPhoneNumber1)));
        assertThat(savedFacility.additionalPhoneNumber2(), is(equalTo(additionalPhoneNumber2)));
        assertThat(savedFacility.additionalPhoneNumber3(), is(equalTo(additionalPhoneNumber3)));
    }

    @Test(expected = FacilityAlreadyFoundException.class)
    public void ShouldNotCreateAFacilityWhenAlreadyExists() throws FacilityAlreadyFoundException {
        String facilityName = "name";
        String country = "country";
        String region = "region";
        String district = "district";
        String province = "province";
        when(mockAllFacilities.facilitiesByName(facilityName)).thenReturn(Arrays.asList(new Facility(new org.motechproject.mrs.model.Facility(facilityName, country, region, district, province))));
        facilityService.create(facilityName, country, region, district, province, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Test
    public void shouldGetAllFacilities() {
        facilityService.facilities();
        verify(mockAllFacilities).facilities();
    }
}

