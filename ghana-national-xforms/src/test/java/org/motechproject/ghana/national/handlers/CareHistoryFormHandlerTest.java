package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.model.MotechEvent;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

public class CareHistoryFormHandlerTest {
    private CareHistoryFormHandler careHistoryFormHandler;
    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private CareService mockCareService;

    @Before
    public void setUp() {
        initMocks(this);
        careHistoryFormHandler = new CareHistoryFormHandler();
        ReflectionTestUtils.setField(careHistoryFormHandler, "careService", mockCareService);
    }

    @Test
    @Ignore
    public void shouldCreateEncounterForCwc() {
        Map<String, Object> parameter = new HashMap<String, Object>();
        Date now = DateUtil.now().toDate();
        parameter.put("formBean", careHistoryFormWithCwcDetails(now));
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.careHistory", parameter);

        careHistoryFormHandler.handleFormEvent(event);

    }

    private CareHistoryForm careHistoryFormWithCwcDetails(Date now) {
        CareHistoryForm careHistoryForm = new CareHistoryForm();
        careHistoryForm.setFacilityId("facilityId");
        careHistoryForm.setStaffId("staffId");
        careHistoryForm.setMotechId("motechId");
        careHistoryForm.setLastOPV("0");
        careHistoryForm.setLastOPVDate(now);
        careHistoryForm.setLastPenta("1");
        careHistoryForm.setLastPentaDate(now);
        careHistoryForm.setLastIPTI("1");
        careHistoryForm.setLastIPTIDate(now);
        careHistoryForm.setLastVitaminADate(now);
        careHistoryForm.setMeaslesDate(now);
        careHistoryForm.setYellowFeverDate(now);
        careHistoryForm.setBcgDate(now);
        return careHistoryForm;
    }
}
