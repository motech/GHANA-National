package org.motechproject.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.helper.FacilityHelper;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.form.SearchFacilityForm;
import org.springframework.context.MessageSource;
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

public class FacilityControllerTest {
    FacilityController facilityController;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    MessageSource mockMessageSource;
    @Mock
    BindingResult mockBindingResult;
    @Mock
    FacilityHelper mockFacilityHelper;

    @Before
    public void setUp() {
        initMocks(this);
        facilityController = new FacilityController(mockFacilityService, mockMessageSource, mockFacilityHelper);
        mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void shouldRenderForm() {
        when(mockFacilityService.facilities()).thenReturn(Arrays.asList(new Facility(new org.motechproject.mrs.model.Facility("facility"))));
        assertThat(facilityController.newFacilityForm(new ModelMap()), is(equalTo("facilities/new")));
    }

    @Test
    public void shouldSaveAFacilityWhenValid() {
        final BindingResult mockBindingResult = mock(BindingResult.class);
        final FacilityController spyFacilitiesController = spy(facilityController);
        ModelMap modelMap = new ModelMap();
        when(mockFacilityService.facilities()).thenReturn(Arrays.asList(new Facility()));
        final HashMap<String, Object> map = new HashMap<String, Object>() {{
            put(FacilityController.CREATE_FACILITY_FORM, new Object());
            put(Constants.COUNTRIES, new Object());
            put(Constants.REGIONS, new Object());
            put(Constants.PROVINCES, new Object());
            put(Constants.DISTRICTS, new Object());
        }};
        when(mockFacilityHelper.locationMap()).thenReturn(map);
        final String result = spyFacilitiesController.createFacility(new FacilityForm(), mockBindingResult, modelMap);

        assertThat(result, is(equalTo("facilities/success")));
        verify(mockFacilityHelper).locationMap();
        assertNotNull(modelMap.get(FacilityController.CREATE_FACILITY_FORM));
        assertNotNull(modelMap.get(Constants.COUNTRIES));
        assertNotNull(modelMap.get(Constants.REGIONS));
        assertNotNull(modelMap.get(Constants.PROVINCES));
        assertNotNull(modelMap.get(Constants.DISTRICTS));
    }

    @Test
    public void testSaveFacilityWhenInValid() throws FacilityAlreadyFoundException {
        final ModelMap modelMap = new ModelMap();
        final String facility = "facility";
        final String country = "country";
        final String region = "region";
        final String district = "district";
        final String province = "province";
        final String message = "Facility already exists.";
        final FacilityController spyFacilitiesController = spy(facilityController);
        doThrow(new FacilityAlreadyFoundException()).when(mockFacilityService).create(facility, country, region, district, province, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        when(mockMessageSource.getMessage("facility_already_exists", null, Locale.getDefault())).thenReturn(message);

        final FacilityForm createFacilityForm = new FacilityForm();
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
            put(FacilityController.CREATE_FACILITY_FORM, new Object());
            put(Constants.COUNTRIES, new Object());
            put(Constants.REGIONS, new Object());
            put(Constants.PROVINCES, new Object());
            put(Constants.DISTRICTS, new Object());
        }};
        when(mockFacilityHelper.locationMap()).thenReturn(map);

        final String result = spyFacilitiesController.createFacility(createFacilityForm, mockBindingResult, modelMap);

        final ArgumentCaptor<FieldError> captor = ArgumentCaptor.forClass(FieldError.class);
        verify(mockBindingResult).addError(captor.capture());
        final FieldError actualFieldError = captor.getValue();
        assertThat(result, is(equalTo("facilities/new")));
        assertThat(actualFieldError.getObjectName(), is(equalTo(FacilityController.CREATE_FACILITY_FORM)));
        assertThat(actualFieldError.getField(), is(equalTo("name")));
        assertThat(actualFieldError.getDefaultMessage(), is(equalTo(message)));
        verify(mockFacilityHelper).locationMap();
        assertNotNull(modelMap.get(FacilityController.CREATE_FACILITY_FORM));
        assertNotNull(modelMap.get(Constants.COUNTRIES));
        assertNotNull(modelMap.get(Constants.REGIONS));
        assertNotNull(modelMap.get(Constants.PROVINCES));
        assertNotNull(modelMap.get(Constants.DISTRICTS));
    }

    @Test
    public void shouldSearchForFacility() throws FacilityAlreadyFoundException {
        final String id = "id";
        final String name = "name";
        final String country = "country";
        final String region = "region";
        final String district = "district";
        final String province = "province";
        final SearchFacilityForm searchFacilityForm = new SearchFacilityForm();
        searchFacilityForm.setFacilityID(id);
        searchFacilityForm.setName(name);
        searchFacilityForm.setCountry(country);
        searchFacilityForm.setRegion(region);
        searchFacilityForm.setCountyDistrict(district);
        searchFacilityForm.setStateProvince(province);

        facilityController.searchFacility(searchFacilityForm, new ModelMap());

        verify(mockFacilityService).searchFacilities(id, name, country, region, district, province);
        verify(mockFacilityHelper).locationMap();
    }

    @Test
    @Ignore
    public void shouldReturnDetailsOfFacilityToEdit() {
        String name = "Dummy Facility";
        String country = "Ghana";
        String region = "Western Ghana";
        String phoneNumber = "0123456789";

        ModelMap modelMap = new ModelMap();
        String facilityId = "123";
        modelMap.addAttribute(FacilityController.FACILITY_ID, facilityId);

        Facility facility = mock(Facility.class);
        when(facility.country()).thenReturn(country);
        when(facility.phoneNumber()).thenReturn(phoneNumber);
        when(facility.region()).thenReturn(region);
        when(facility.name()).thenReturn(name);

        when(mockFacilityService.getFacility(facilityId)).thenReturn(facility);

        String editFormName = facilityController.editFacilityForm(modelMap);

        assertThat(editFormName, is(FacilityController.EDIT_FACILITY));
        FacilityForm facilityForm = createFaciliy(name, country, region, phoneNumber);
        assertThat((FacilityForm) modelMap.get("facilityForm"), is(equalTo(facilityForm)));
    }

    private FacilityForm createFaciliy(String name, String country, String region, String phoneNumber) {
        FacilityForm facilityForm = new FacilityForm();
        facilityForm.setName(name);
        facilityForm.setCountry(country);
        facilityForm.setRegion(region);
        facilityForm.setPhoneNumber(phoneNumber);
        return facilityForm;
    }
}
