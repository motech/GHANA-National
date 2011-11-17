package org.motechproject.ghana.national.handlers;

import org.motechproject.MotechException;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.Facility;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.safeToString;


@Component
public class PatientRegistrationFormHandler implements FormPublishHandler {

    public static final String FORM_BEAN = "formBean";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PatientService patientService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.registerPatient-jf")
    @LoginAsAdmin
    public void handleFormEvent(MotechEvent event) {
        RegisterClientForm registerClientForm = (RegisterClientForm) event.getParameters().get(FORM_BEAN);
        org.motechproject.mrs.model.Patient mrsPatient = new org.motechproject.mrs.model.Patient(registerClientForm.getMotechId(), registerClientForm.getFirstName(),
                registerClientForm.getMiddleName(), registerClientForm.getLastName(), registerClientForm.getPrefferedName(), registerClientForm.getDateOfBirth(), registerClientForm.getEstimatedBirthDate(),
                registerClientForm.getSex(), registerClientForm.getAddress(), getPatientAttributes(registerClientForm), new Facility(registerClientForm.getFacilityId()));
        try {
            patientService.registerPatient(new Patient(mrsPatient), registerClientForm.getRegistrantType(), registerClientForm.getMotherMotechId());
        } catch (Exception e) {
            throw new MotechException("Exception while saving patient", e);
        }
    }

    private List<Attribute> getPatientAttributes(RegisterClientForm registerClientForm) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute(), safeToString(registerClientForm.getNhisExpires())));
        attributes.add(new Attribute(PatientAttributes.NHIS_NUMBER.getAttribute(), registerClientForm.getNhis()));
        attributes.add(new Attribute(PatientAttributes.INSURED.getAttribute(), safeToString(registerClientForm.getInsured())));
        return attributes;
    }
}
