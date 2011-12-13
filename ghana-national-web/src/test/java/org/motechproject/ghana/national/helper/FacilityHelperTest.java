package org.motechproject.ghana.national.helper;

import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.FacilityVO;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mrs.model.MRSFacility;
import org.springframework.test.util.ReflectionTestUtils;

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

public class FacilityHelperTest {

    @Mock
    FacilityService mockFacilityService;

    @SuppressWarnings("unchecked")
    @Test
    public void testPopulateFacilities() {
        initMocks(this);
        FacilityHelper facilityHelper = new FacilityHelper();
        ReflectionTestUtils.setField(facilityHelper, "facilityService", mockFacilityService);
        List<Facility> facilities = populateData();
        when(mockFacilityService.facilities()).thenReturn(facilities);
        Map modelMap = facilityHelper.locationMap();
        assertThat((List<String>) modelMap.get(Constants.COUNTRIES), is(equalTo(Arrays.asList("Utopia"))));
        assertThat((Map<String, TreeSet<String>>) modelMap.get(Constants.REGIONS), is(equalTo(regions())));
        assertThat((Map<String, TreeSet<String>>) modelMap.get(Constants.DISTRICTS), is(equalTo(districts())));
        assertThat((Map<String, TreeSet<String>>) modelMap.get(Constants.PROVINCES), is(equalTo(provinces())));
        assertThat((List<FacilityVO>) modelMap.get(Constants.FACILITIES), is(equalTo(facilities())));
    }

    static Map<String, TreeSet<String>> regions() {
        return new HashMap<String, TreeSet<String>>() {{
            put("Utopia", new TreeSet<String>() {{
                add("Region 1");
                add("Region 3");
                add("Region 4");
            }});
        }};
    }

    static Map<String, TreeSet<String>> districts() {
        return new HashMap<String, TreeSet<String>>() {{
            put("Region 1", new TreeSet<String>() {{
                add("District 1");
            }});
            put("Region 3", new TreeSet<String>() {{
                add("District 6");
            }});
            put("Region 4", new TreeSet<String>() {{
                add("");
            }});
        }};
    }

    static Map<String, TreeSet<String>> provinces() {
        return new HashMap<String, TreeSet<String>>() {{
            put("District 1", new TreeSet<String>() {{
                add("Province 1");
                add("Province 2");
            }});

            put("District 6", new TreeSet<String>() {{
                add("");
            }});

        }};
    }

    static List<FacilityVO> facilities() {
        return new ArrayList<FacilityVO>() {{
            add(new FacilityVO(null, "Facility 1", "Province 1"));
            add(new FacilityVO(null, "Facility 2", "Province 2"));
            add(new FacilityVO(null, "Facility 3", "District 6"));
            add(new FacilityVO(null, "Facility 4", "Region 4"));
        }};
    }

    private List<Facility> populateData() {
        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(new Facility(new MRSFacility("Facility 1", "Utopia", "Region 1", "District 1", "Province 1")));
        facilities.add(new Facility(new MRSFacility("Facility 2", "Utopia", "Region 1", "District 1", "Province 2")));
        facilities.add(new Facility(new MRSFacility("Facility 3", "Utopia", "Region 3", "District 6", null)));
        facilities.add(new Facility(new MRSFacility("Facility 4", "Utopia", "Region 4", null, null)));
        return facilities;
    }
}
