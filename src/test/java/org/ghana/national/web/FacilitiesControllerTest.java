package org.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.services.Facility;
import org.motechproject.mrs.services.FacilityService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacilitiesControllerTest {

    FacilitiesController facilitiesController;

    @Mock
    FacilityService mockFacilityService;

    @Before
    public void setUp() {
        initMocks(this);
        facilitiesController = new FacilitiesController();
        ReflectionTestUtils.setField(facilitiesController, "facilityService", mockFacilityService);
    }

    @Test
    public void shouldRenderForm() {
        when(mockFacilityService.getFacilities()).thenReturn(Arrays.asList(new Facility("facility")));
        assertThat(facilitiesController.newFacilityForm(new ModelMap()), is(equalTo("common/facilities/new")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPopulateFacilities() {
        List<Facility> facilities = populateData();
        ModelMap modelMap = facilitiesController.populateLocation(facilities, new ModelMap());
        assertThat((List<String>) modelMap.get("countries"), is(equalTo(Arrays.asList("Utopia"))));
        assertThat((Map<String, TreeSet<String>>) modelMap.get("regions"), is(equalTo(regions())));
        assertThat((Map<String, TreeSet<String>>) modelMap.get("districts"), is(equalTo(districts())));
        assertThat((Map<String, TreeSet<String>>) modelMap.get("provinces"), is(equalTo(provinces())));
    }

    static Map<String, TreeSet<String>> regions() {
        return new HashMap<String, TreeSet<String>>() {{
            put("Utopia", new TreeSet<String>() {{
                add("Region 1");
                add("Region 2");
                add("Region 3");
                add("Region 4");
            }});
        }};
    }

    static Map<String, TreeSet<String>> districts() {
        return new HashMap<String, TreeSet<String>>() {{
            put("Region 1", new TreeSet<String>() {{
                add("District 1");
                add("District 2");
            }});
            put("Region 2", new TreeSet<String>() {{
                add("District 4");
                add("District 3");
            }});
            put("Region 3", new TreeSet<String>() {{
                add("District 5");
                add("District 6");
                add("District 7");
            }});
            put("Region 4", new TreeSet<String>() {{
                add("null");
            }});
        }};
    }

    static Map<String, TreeSet<String>> provinces() {
        return new HashMap<String, TreeSet<String>>() {{
            put("District 1", new TreeSet<String>() {{
                add("Province 1");
                add("Province 2");
            }});
            put("District 4", new TreeSet<String>() {{
                add("Province 6");
            }});
            put("District 5", new TreeSet<String>() {{
                add("Province 7");
            }});
            put("District 2", new TreeSet<String>() {{
                add("Province 3");
            }});
            put("District 3", new TreeSet<String>() {{
                add("Province 4");
                add("Province 5");
            }});
            put("District 6", new TreeSet<String>() {{
                add("null");
            }});
            put("District 7", new TreeSet<String>() {{
                add("Province 8");
            }});
        }};
    }

    private List<Facility> populateData() {
        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(new Facility("Facility 1", "Utopia", "Region 1", "District 1", "Province 1"));
        facilities.add(new Facility("Facility 2", "Utopia", "Region 1", "District 1", "Province 2"));
        facilities.add(new Facility("Facility 3", "Utopia", "Region 1", "District 2", "Province 3"));
        facilities.add(new Facility("Facility 4", "Utopia", "Region 2", "District 3", "Province 4"));
        facilities.add(new Facility("Facility 5", "Utopia", "Region 2", "District 3", "Province 5"));
        facilities.add(new Facility("Facility 6", "Utopia", "Region 2", "District 4", "Province 6"));
        facilities.add(new Facility("Facility 7", "Utopia", "Region 3", "District 5", "Province 7"));
        facilities.add(new Facility("Facility 8", "Utopia", "Region 3", "District 7", "Province 8"));
        facilities.add(new Facility("Facility 9", "Utopia", "Region 3", "District 6", "null"));
        facilities.add(new Facility("Facility 9", "Utopia", "Region 4", "null", "null"));
        facilities.add(new Facility("Unknown Location", "", "null", "null", ""));
        return facilities;
    }
}
