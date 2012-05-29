package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.model.Time;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeFormValidatorTest {
    private MobileMidwifeFormValidator mobileMidwifeFormValidator;

    @Mock
    private MobileMidwifeValidator mobileMidwifeValidator;

    @Before
    public void setUp(){
        initMocks(this);
        mobileMidwifeFormValidator = new MobileMidwifeFormValidator();
        ReflectionTestUtils.setField(mobileMidwifeFormValidator, "mobileMidwifeValidator", mobileMidwifeValidator);
    }

    @Test
    public void shouldValidateMobileMidwifeForm() {
        String patientId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        Time timeOfDay = new Time(22, 10);
        MobileMidwifeForm formBean = new MobileMidwifeBuilder().patientId(patientId).staffId(staffId).facilityId(facilityId)
                .consent(true).format("PERS_VOICE").timeOfDay(timeOfDay).buildMobileMidwifeForm();

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean);
        mobileMidwifeFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);

        ArgumentCaptor<MobileMidwifeEnrollment> mobileMidwifeEnrollmentCaptor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mobileMidwifeValidator).validate(mobileMidwifeEnrollmentCaptor.capture(), eq(formBeans), eq(formBeans));
        MobileMidwifeEnrollment actualEnrollment = mobileMidwifeEnrollmentCaptor.getValue();

        assertThat(actualEnrollment.getFacilityId(), is(equalTo(facilityId)));
        assertThat(actualEnrollment.getPatientId(), is(equalTo(patientId)));
        assertThat(actualEnrollment.getStaffId(), is(equalTo(staffId)));
        assertTrue(actualEnrollment.getConsent());
        assertThat(actualEnrollment.getMedium(), is(Medium.VOICE));
        assertThat(actualEnrollment.getTimeOfDay(), is(timeOfDay));
    }
}
