package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.service.ANCService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.web.form.ANCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.Date;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCControllerTest {
    private ANCController ancController;
    @Mock
    private FacilityHelper facilityHelper;
    @Mock
    private ANCService ancService;

    @Before
    public void setUp() {
        initMocks(this);
        ancController = new ANCController(facilityHelper, ancService);
    }

    @Test
    @Ignore("wip")
    public void shouldAddPageAttributesAndDisplayNewForm() {
        ModelMap modelMap = new ModelMap();
        String motechPatientId = "motechPatientId";
        String ancUrl = ancController.enroll(motechPatientId, modelMap);
        ANCEnrollmentForm ancEnrollmentForm = (ANCEnrollmentForm) modelMap.get("ancEnrollmentForm");

        assertThat(ancUrl, is(equalTo(ANCController.ANC_URL)));
        assertTrue("Form attributes are not equal", reflectionEquals(ancEnrollmentForm, new ANCEnrollmentForm(motechPatientId)));
        assertTrue(reflectionEquals(modelMap.get("careHistories"), Arrays.asList("TT", "IPT")));
        assertTrue(reflectionEquals(modelMap.get("lastIPT"), Arrays.asList("IPT 1", "IPT 2", "IPT 3")));
        assertTrue(reflectionEquals(modelMap.get("lastTT"), Arrays.asList("TT 1", "TT 2", "TT 3", "TT 4", "TT 5")));
        verify(facilityHelper).locationMap();
    }

    @Test
    @Ignore("wip")
    public void shouldSaveANCEnrollmentDetails() {
        FacilityForm facilityForm = new FacilityForm();
        ANCEnrollmentForm ancEnrollmentForm = new ANCEnrollmentForm("patientId", "serialNUMBER", DateUtil.now().toDate());
        ancEnrollmentForm.setLastIPT("IPT 1");
        ancEnrollmentForm.setLastIPTDate(new Date());
        ancEnrollmentForm.setLastTT("TT 1");
        ancEnrollmentForm.setLastTTDate(new Date());
        ancEnrollmentForm.setEstimatedDateOfDelivery(new Date());
        ancEnrollmentForm.setGravida(10);
        ancEnrollmentForm.setParity(10);
        ancEnrollmentForm.setHeight(Float.valueOf("170.5"));
        ancEnrollmentForm.setRegistrationToday(RegistrationToday.TODAY);
        ancEnrollmentForm.setFacilityForm(facilityForm);

        ModelMap modelMap = new ModelMap();

        ancController.save(ancEnrollmentForm, modelMap);

        ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
//        verify(ancService).save(captor.capture());
        ANCVO vo = captor.getValue();
        assertVo(ancEnrollmentForm, vo);
    }

    private void assertVo(ANCEnrollmentForm ancEnrollmentForm, ANCVO vo) {
        assertThat(ancEnrollmentForm.getMotechPatientId(), is(equalTo(vo.getMotechPatientId())));
        assertThat(ancEnrollmentForm.getSerialNumber(), is(equalTo(vo.getSerialNumber())));
        assertThat(ancEnrollmentForm.getEstimatedDateOfDelivery(), is(equalTo(vo.getEstimatedDateOfDelivery())));
        assertThat(ancEnrollmentForm.getFacilityForm().getFacilityId(), is(equalTo(vo.getFacilityId())));
        assertThat(ancEnrollmentForm.getGravida(), is(equalTo(vo.getGravida())));
        assertThat(ancEnrollmentForm.getParity(), is(equalTo(vo.getParity())));
        assertThat(ancEnrollmentForm.getHeight(), is(equalTo(vo.getHeight())));
//        assertThat(ancEnrollmentForm.getLastIPT(), is(equalTo(vo.getLastIPT())));
//        assertThat(ancEnrollmentForm.getLastTT(), is(equalTo(vo.getLastTT())));
//        assertThat(ancEnrollmentForm.getLastTTDate(), is(equalTo(vo.getLastTTDate())));
//        assertThat(ancEnrollmentForm.getLastIPTDate(), is(equalTo(vo.getLastIPTDate())));
//        assertThat(ancEnrollmentForm.getRegistrationToday(), is(equalTo(vo.getRegistrationToday())));
//        assertThat(ancEnrollmentForm.getRegistrationDate(), is(equalTo(vo.getRegistrationDate())));
    }
}
