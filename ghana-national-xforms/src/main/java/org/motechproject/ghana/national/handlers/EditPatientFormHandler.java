package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.EditClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.equalTo;


@Component
public class EditPatientFormHandler implements FormPublishHandler {

    public static final String FORM_BEAN = "formBean";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PatientService patientService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private StaffService staffService;
    private static final String PATIENTEDITVISIT = "PATIENTEDITVISIT";

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.editPatient")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {

        EditClientForm editClientForm = (EditClientForm) event.getParameters().get(FORM_BEAN);

        String motechId = editClientForm.getMotechId();
        Patient existingPatient = patientService.getPatientByMotechId(motechId);
        MRSPatient existingMRSPatient = existingPatient.getMrsPatient();


        String mrsFacilityIdFromForm = null;
        String facilityMotechIdFromForm = null;
        MRSFacility patientMRSFacilityFromForm = null;

        if (editClientForm.getFacilityId() != null) {
            facilityMotechIdFromForm = editClientForm.getFacilityId();
            Facility patientFacilityFromForm = facilityService.getFacilityByMotechId(facilityMotechIdFromForm);
            patientMRSFacilityFromForm = patientFacilityFromForm.getMrsFacility();
            mrsFacilityIdFromForm = patientFacilityFromForm.getMrsFacilityId();
        }

        MRSFacility existingOrUpdatedFacility = existingMRSPatient.getFacility();

        Date dateOfBirth = editClientForm.getDateOfBirth();
        MRSPerson mrsPerson = existingMRSPatient.getPerson();
        String firstName = editClientForm.getFirstName();
        String middleName = editClientForm.getMiddleName();
        String lastName = editClientForm.getLastName();
        String preferredName = editClientForm.getPrefferedName();
        String sex = editClientForm.getSex();
        String motherMotechId = editClientForm.getMotherMotechId();
        String address = editClientForm.getAddress();
        String nhisNumber = editClientForm.getNhis();
        String phoneNumber = editClientForm.getPhoneNumber();
        Date nhisExpires = editClientForm.getNhisExpires();

        String facilityMotechIdWherePatientWasEdited = editClientForm.getUpdatePatientFacilityId();
        String facilityIdWherePatientWasEdited = facilityService.getFacilityByMotechId(facilityMotechIdWherePatientWasEdited).getMrsFacilityId();

        if (facilityMotechIdFromForm != null) {
            existingOrUpdatedFacility = new MRSFacility(mrsFacilityIdFromForm, patientMRSFacilityFromForm.getName()
                    , patientMRSFacilityFromForm.getCountry(), patientMRSFacilityFromForm.getRegion(),
                    patientMRSFacilityFromForm.getCountyDistrict(), patientMRSFacilityFromForm.getStateProvince());
        }


        if (firstName != null) {
            mrsPerson.firstName(firstName);
        }

        if (middleName != null) {
            mrsPerson.middleName(middleName);
        }

        if (lastName != null) {
            mrsPerson.lastName(lastName);
        }

        if (preferredName != null) {
            mrsPerson.preferredName(preferredName);
        }

        if (dateOfBirth != null) {
            mrsPerson.dateOfBirth(dateOfBirth);
        }

        if (sex != null) {
            mrsPerson.gender(sex);
        }

        if (address != null) {
            mrsPerson.address(address);
        }

        if (phoneNumber != null) {
            List<Attribute> attributes = mrsPerson.getAttributes();
            attributes.remove(getAttribute(attributes, PatientAttributes.PHONE_NUMBER.getAttribute()));
            mrsPerson.addAttribute(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber));
        }

        if (nhisNumber != null) {
            List<Attribute> attributes = mrsPerson.getAttributes();
            attributes.remove(getAttribute(attributes, PatientAttributes.NHIS_NUMBER.getAttribute()));
            mrsPerson.addAttribute(new Attribute(PatientAttributes.NHIS_NUMBER.getAttribute(), nhisNumber));
        }

        if (nhisExpires != null) {
            List<Attribute> attributes = mrsPerson.getAttributes();
            attributes.remove(getAttribute(attributes, PatientAttributes.NHIS_EXPIRY_DATE.getAttribute()));
            mrsPerson.addAttribute(new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute(), new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD).format(nhisExpires)));
        }

        MRSPatient updatedMRSPatient = new MRSPatient(motechId, existingMRSPatient.getPerson(), existingOrUpdatedFacility);

        String staffId = editClientForm.getStaffId();
        List<MRSUser> mrsUsers = staffService.searchStaff(staffId, "", "", "", "", "");
        MRSUser mrsUser = mrsUsers.get(0);
        MRSPerson staffPerson = mrsUser.getPerson();
        MRSEncounter mrsEncounter = new MRSEncounter(staffPerson.getId(), mrsUser.getId(), facilityIdWherePatientWasEdited,
                editClientForm.getDate(), existingMRSPatient.getId(), null, PATIENTEDITVISIT);

        try {
            patientService.updatePatient(new Patient(updatedMRSPatient), null, motherMotechId);
            patientService.saveEncounter(mrsEncounter);
        } catch (ParentNotFoundException ignored) {
        }
    }

    public Attribute getAttribute(List<Attribute> attributes, String key) {
        List<Attribute> filteredItems = select(attributes, having(on(Attribute.class).name(), equalTo(key)));
        return isNotEmpty(filteredItems) ? filteredItems.get(0) : null;
    }
}
