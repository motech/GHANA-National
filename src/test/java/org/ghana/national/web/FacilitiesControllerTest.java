package org.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.ghana.national.domain.Facility;
import org.ghana.national.exception.FacilityAlreadyFoundException;
import org.ghana.national.service.FacilityService;
import org.ghana.national.tools.Constants;
import org.ghana.national.web.form.CreateFacilityForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacilitiesControllerTest {
    FacilitiesController facilitiesController;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    MessageSource mockMessageSource;

    @Before
    public void setUp() {
        initMocks(this);
        facilitiesController = new FacilitiesController();
        ReflectionTestUtils.setField(facilitiesController, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(facilitiesController, "messageSource", mockMessageSource);
    }

    @Test
    public void shouldRenderForm() {
        when(mockFacilityService.facilities()).thenReturn(Arrays.asList(new Facility(new org.motechproject.mrs.model.Facility("facility"))));
        assertThat(facilitiesController.newFacilityForm(new ModelMap()), is(equalTo("facilities/new")));
    }

    @Test
    public void shouldSaveAFacilityWhenValid() {
        final BindingResult mockBindingResult = mock(BindingResult.class);
        final FacilitiesController spyFacilitiesController = spy(facilitiesController);
        ModelMap modelMap = new ModelMap();
        when(mockFacilityService.facilities()).thenReturn(Arrays.asList(new Facility()));
        final HashMap<String, Object> map = new HashMap<String, Object>() {{
            put(Constants.CREATE_FACILITY_FORM, new Object());
            put(Constants.COUNTRIES, new Object());
            put(Constants.REGIONS, new Object());
            put(Constants.PROVINCES, new Object());
            put(Constants.DISTRICTS, new Object());
        }};
        when(mockFacilityService.populateFacilityData()).thenReturn(map);
        final String result = spyFacilitiesController.createFacility(new CreateFacilityForm(), mockBindingResult, modelMap);

        assertThat(result, is(equalTo("facilities/success")));
        verify(mockFacilityService).populateFacilityData();
        assertNotNull(modelMap.get(Constants.CREATE_FACILITY_FORM));
        assertNotNull(modelMap.get(Constants.COUNTRIES));
        assertNotNull(modelMap.get(Constants.REGIONS));
        assertNotNull(modelMap.get(Constants.PROVINCES));
        assertNotNull(modelMap.get(Constants.DISTRICTS));
    }

    @Test
    public void testSaveFacilityWhenInValid() throws FacilityAlreadyFoundException {
        final BindingResult mockBindingResult = mock(BindingResult.class);
        final ModelMap modelMap = new ModelMap();
        final String facility = "facility";
        final String country = "country";
        final String region = "region";
        final String district = "district";
        final String province = "province";
        final String message = "Facility already exists.";
        final FacilitiesController spyFacilitiesController = spy(facilitiesController);
        doThrow(new FacilityAlreadyFoundException()).when(mockFacilityService).create(facility, country, region, district, province, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        when(mockMessageSource.getMessage("facility_already_exists", null, Locale.getDefault())).thenReturn(message);

        final CreateFacilityForm createFacilityForm = new CreateFacilityForm();
        createFacilityForm.setName(facility);
        createFacilityForm.setCountry(country);
        createFacilityForm.setRegion(region);
        createFacilityForm.setCountyDistrict(district);
        createFacilityForm.setStateProvince(province);
        createFacilityForm.setPhoneNumber(StringUtils.EMPTY);
        createFacilityForm.setAdditionalPhoneNumber1(StringUtils.EMPTY);
        createFacilityForm.setAdditionalPhoneNumber2(StringUtils.EMPTY);
        createFacilityForm.setAdditionalPhoneNumber3(StringUtils.EMPTY);

        final HashMap<String, Object> map = new HashMap<String, Object>() {{
            put(Constants.CREATE_FACILITY_FORM, new Object());
            put(Constants.COUNTRIES, new Object());
            put(Constants.REGIONS, new Object());
            put(Constants.PROVINCES, new Object());
            put(Constants.DISTRICTS, new Object());
        }};
        when(mockFacilityService.populateFacilityData()).thenReturn(map);

        final String result = spyFacilitiesController.createFacility(createFacilityForm, mockBindingResult, modelMap);

        final ArgumentCaptor<FieldError> captor = ArgumentCaptor.forClass(FieldError.class);
        verify(mockBindingResult).addError(captor.capture());
        final FieldError actualFieldError = captor.getValue();
        assertThat(result, is(equalTo("facilities/new")));
        assertThat(actualFieldError.getObjectName(), is(equalTo(Constants.CREATE_FACILITY_FORM)));
        assertThat(actualFieldError.getField(), is(equalTo("name")));
        assertThat(actualFieldError.getDefaultMessage(), is(equalTo(message)));
        verify(mockFacilityService).populateFacilityData();
        assertNotNull(modelMap.get(Constants.CREATE_FACILITY_FORM));
        assertNotNull(modelMap.get(Constants.COUNTRIES));
        assertNotNull(modelMap.get(Constants.REGIONS));
        assertNotNull(modelMap.get(Constants.PROVINCES));
        assertNotNull(modelMap.get(Constants.DISTRICTS));
    }
}
