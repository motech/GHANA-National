package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeFormValidatorTest {
    private MobileMidwifeFormValidator mobileMidwifeFormValidator;

    @Mock
    private PatientService patientService;

    @Before
    public void setUp(){
        initMocks(this);
        mobileMidwifeFormValidator = new MobileMidwifeFormValidator();
        ReflectionTestUtils.setField(mobileMidwifeFormValidator, "patientService", patientService);
    }

    @Test
    public void shouldValidateIfPatientIsAlive(){
        Patient patientMock = mock(Patient.class);
        String motechId = "motechId";
        when(patientService.getPatientByMotechId(motechId)).thenReturn(patientMock);

        MRSPatient mrsPatient = mock(MRSPatient.class);
        when(patientMock.getMrsPatient()).thenReturn(mrsPatient);

        MRSPerson mrsPerson = mock(MRSPerson.class);
        when(mrsPatient.getPerson()).thenReturn(mrsPerson);

        when(mrsPerson.isDead()).thenReturn(true);

        RegisterClientForm formBean = new RegisterClientForm();
        formBean.setMotechId(motechId);
        assertThat(mobileMidwifeFormValidator.validate(formBean), hasItem(new FormError("motechId", MobileMidwifeFormValidator.IS_NOT_ALIVE)));
    }
}
