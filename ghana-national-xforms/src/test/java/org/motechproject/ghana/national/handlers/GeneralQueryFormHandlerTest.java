package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.GeneralQueryForm;
import org.motechproject.ghana.national.domain.GeneralQueryType;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentsQueryService;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class GeneralQueryFormHandlerTest {
    @InjectMocks
    GeneralQueryFormHandler generalQueryFormHandler;

    @Mock
    EnrollmentsQueryService mockEnrollmentsQueryService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnUpcomingDeliveries(){
        final GeneralQueryForm generalQueryForm = new GeneralQueryForm();
        String facilityId = "facilityId";
        generalQueryForm.setFacilityId(facilityId);
        generalQueryForm.setResponsePhoneNumber("phone-number");
        generalQueryForm.setQueryType(GeneralQueryType.UPCOMING_DELIVERIES);

        MotechEvent event = new MotechEvent("form.validation.successful.NurseQuery.GeneralQuery", new HashMap<String, Object>() {{
            put("formBean", generalQueryForm);
        }});
        generalQueryFormHandler.handleFormEvent(event);

        EnrollmentsQuery enrollmentsQuery = new EnrollmentsQuery().havingMetadata("facilityId", facilityId).havingSchedule("");
        verify(mockEnrollmentsQueryService).search(enrollmentsQuery);
    }
}
