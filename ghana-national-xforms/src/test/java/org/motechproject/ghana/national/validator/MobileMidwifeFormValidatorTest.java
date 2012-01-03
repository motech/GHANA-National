package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;
import static org.motechproject.ghana.national.validator.MobileMidwifeFormValidator.*;

public class MobileMidwifeFormValidatorTest {
    private MobileMidwifeFormValidator mobileMidwifeFormValidator;

    @Mock
    private PatientService patientService;
    @Mock
    private FacilityService facilityService;
    @Mock
    private StaffService staffService;

    @Before
    public void setUp(){
        initMocks(this);
        mobileMidwifeFormValidator = new MobileMidwifeFormValidator();
        ReflectionTestUtils.setField(mobileMidwifeFormValidator, "patientService", patientService);
        ReflectionTestUtils.setField(mobileMidwifeFormValidator, "facilityService", facilityService);
        ReflectionTestUtils.setField(mobileMidwifeFormValidator, "staffService", staffService);
    }

    @Test
    public void shouldValidateIfPatientIsAlive(){
        String motechId = "1234567";
        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(motechId, true);

        MobileMidwifeForm formBean = new MobileMidwifeForm();
        formBean.setPatientId(motechId);
        assertThat(mobileMidwifeFormValidator.validate(formBean), hasItem(new FormError(PATIENT_ID, MobileMidwifeFormValidator.IS_NOT_ALIVE)));

        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(motechId, false);
        assertThat(mobileMidwifeFormValidator.validate(formBean), not(hasItem(new FormError(PATIENT_ID, MobileMidwifeFormValidator.IS_NOT_ALIVE))));
    }

    private MRSPerson setupPatientServiceMockToReturnIfPatientIsAliveOrDead(String motechId, boolean isDead) {
        Patient patientMock = mock(Patient.class);
        when(patientService.getPatientByMotechId(motechId)).thenReturn(patientMock);

        MRSPatient mrsPatient = mock(MRSPatient.class);
        when(patientMock.getMrsPatient()).thenReturn(mrsPatient);

        MRSPerson mrsPerson = mock(MRSPerson.class);
        when(mrsPatient.getPerson()).thenReturn(mrsPerson);

        when(mrsPerson.isDead()).thenReturn(isDead);
        return mrsPerson;
    }

    @Test
    public void shouldValidateIfTheFacilityExists(){
        String patientMotechId = "1234567";
        String facilityMotechId = "012345678";

        Facility facility = mock(Facility.class);

        MobileMidwifeForm formBean = new MobileMidwifeForm();
        formBean.setPatientId(patientMotechId);
        formBean.setFacilityId(facilityMotechId);

        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(null);
        assertThat(mobileMidwifeFormValidator.validate(formBean), hasItem(new FormError(FACILITY_ID, NOT_FOUND)));

        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(facility);
        assertThat(mobileMidwifeFormValidator.validate(formBean), not(hasItem(new FormError(FACILITY_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidateIfPatientExists(){
        String patientId = "patientId";
        when(patientService.getPatientByMotechId(patientId)).thenReturn(null);

        MobileMidwifeForm formBean = new MobileMidwifeForm();
        formBean.setPatientId(patientId);
        assertThat(mobileMidwifeFormValidator.validate(formBean), hasItem(new FormError(PATIENT_ID, NOT_FOUND)));

        setupPatientServiceMockToReturnIfPatientIsAliveOrDead(patientId, false);
        assertThat(mobileMidwifeFormValidator.validate(formBean), not(hasItem(new FormError(PATIENT_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidateIfStaffExists(){
        String staffId = "1234567";
        when(staffService.getUserById(staffId)).thenReturn(null);

        MobileMidwifeForm formBean = new MobileMidwifeForm();
        formBean.setStaffId(staffId);

        assertThat(mobileMidwifeFormValidator.validate(formBean), hasItem(new FormError(STAFF_ID, NOT_FOUND)));

        MRSUser staff = mock(MRSUser.class);
        when(staffService.getUserById(staffId)).thenReturn(staff);

        assertThat(mobileMidwifeFormValidator.validate(formBean), not(hasItem(new FormError(STAFF_ID, NOT_FOUND))));
    }
}
