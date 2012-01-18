package org.motechproject.ghana.national.functional.Generator;


import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.web.PatientController;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.util.Date;

import static org.mockito.Mockito.mock;

@Component
public class PatientGenerator {


    @Autowired
    PatientController patientController;

    public PatientGenerator() {
    }

    public PatientGenerator(PatientController patientController) {
        this.patientController=patientController;
    }

    @LoginAsAdmin
    @ApiSession
    public String createPatientAndReturnPatientId(String facilityId){
        PatientForm patientForm = new PatientForm().setFirstName("firstName").setLastName("lastName").setDateOfBirth(new Date()).setSex("Female").setFacilityId(facilityId)
                .setRegistrationMode(RegistrationType.AUTO_GENERATE_ID).setEstimatedDateOfBirth(false);
        BindingResult mockBindingResult = mock(BindingResult.class);
        ModelMap modelMap = new ModelMap();
        patientController.createPatient(patientForm, mockBindingResult, modelMap);
        PatientForm createdPatientForm = (PatientForm) modelMap.get(PatientController.PATIENT_FORM);
        return createdPatientForm.getMotechId();
    }
}
