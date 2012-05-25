package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

@Component
public class FormValidator {

    @Autowired
    private PatientService patientService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private AllEncounters allEncounters;


    public Patient getPatient(String motechId){
        return patientService.getPatientByMotechId(motechId);
    }

    public static final String FACILITY_ID = "facilityId";
    public static final String STAFF_ID = "staffId";

    public List<FormError> validateIfPatientExists(Patient patient) {
        if (patient == null) {
            return new ArrayList<FormError>() {{
                add(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND));
            }};
        }
        return new ArrayList<FormError>();
    }


    public List<FormError> validateIfFacilityExists(String facilityId) {
        if (facilityService.getFacilityByMotechId(facilityId) == null) {
            return new ArrayList<FormError>() {{
                add(new FormError(FACILITY_ID, NOT_FOUND));
            }};
        }
        return new ArrayList<FormError>();
    }

    public List<FormError> validateIfStaffExists(final String staffId) {
        if (staffService.getUserByEmailIdOrMotechId(staffId) == null) {
            return new ArrayList<FormError>() {{
                add(new FormError(STAFF_ID, NOT_FOUND));
            }};
        }
        return new ArrayList<FormError>();
    }
}
