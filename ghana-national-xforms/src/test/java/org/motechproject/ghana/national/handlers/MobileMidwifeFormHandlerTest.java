package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.bean.MobileMidwifeFormTest;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.model.MotechEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class MobileMidwifeFormHandlerTest {

    @Mock
    private MobileMidwifeService mockMobileMidwifeService;

    MobileMidwifeFormHandler formHandler;

    @Before
    public void setUp() {
         initMocks(this);
        formHandler = new MobileMidwifeFormHandler();
        ReflectionTestUtils.setField(formHandler, "mobileMidwifeService", mockMobileMidwifeService);
    }

    @Test
    public void shouldSaveMobileMidwifeEnrollmentForm() {
        final String staffId = "11";
        final String facilityId = "11";
        final MobileMidwifeForm form = MobileMidwifeFormTest.setupFormData(staffId, facilityId);

        formHandler.handleFormEvent(new MotechEvent("", new HashMap<String, Object>(){{
            put("formBean", form);
        }}));

        final ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeService).register(captor.capture());
        final MobileMidwifeEnrollment enrollment = captor.getValue();

        MobileMidwifeFormTest.assertFormWithEnrollment(form, enrollment);
    }
}
