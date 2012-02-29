package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PregnancyTerminationService;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.model.MotechEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PregnancyTerminationFormHandlerTest {
    private PregnancyTerminationFormHandler pregnancyTerminationFormHandler;


    @Mock
    PregnancyTerminationService mockPregnancyTerminationService;

    @Mock
    MobileMidwifeService mockMobileMidwifeService;

    @Before
    public void setUp() {
        initMocks(this);
        pregnancyTerminationFormHandler = new PregnancyTerminationFormHandler();
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler,"pregnancyTerminationService", mockPregnancyTerminationService);
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler,"mobileMidwifeService", mockMobileMidwifeService);

    }

    @Test
    public void shouldHandlePregnancyTerminationEvent(){
        String facilityId = "facilityId";
        String staffId = "staffId";
        String motechId = "motechId";
        PregnancyTerminationForm form=pregnancyTerminationForm(motechId, staffId, facilityId);

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Constants.FORM_BEAN,form);
        MotechEvent event=new MotechEvent("form.validation.successful.NurseDataEntry.PregnancyTermination",parameters);

        pregnancyTerminationFormHandler.handleFormEvent(event);

        verify(mockPregnancyTerminationService).terminatePregnancy(any(PregnancyTerminationRequest.class));
        verify(mockMobileMidwifeService).unRegister(motechId);

    }



    private PregnancyTerminationForm pregnancyTerminationForm(String motechId, String staffId, String facilityId) {
        PregnancyTerminationForm pregnancyTerminationForm = new PregnancyTerminationForm();
        pregnancyTerminationForm.setFacilityId(facilityId);
        pregnancyTerminationForm.setMotechId(motechId);
        pregnancyTerminationForm.setStaffId(staffId);
        return pregnancyTerminationForm;
    }
}
