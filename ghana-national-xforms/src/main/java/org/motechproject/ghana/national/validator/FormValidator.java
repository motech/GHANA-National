package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

@Component
public class FormValidator {

    @Autowired
    private PatientService patientService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private StaffService staffService;

    public static final String IS_NOT_ALIVE = "is not alive";
    public static final String PATIENT_ID = "patientId";
    public static final String FACILITY_ID = "facilityId";
    public static final String STAFF_ID = "staffId";


    public void validatePatient(List<FormError> formErrors, String patientId) {
        Patient patient = patientService.getPatientByMotechId(patientId);
        if (patient == null) {
            formErrors.add(new FormError(PATIENT_ID, NOT_FOUND));
        } else if (patient.getMrsPatient().getPerson().isDead()) {
            formErrors.add(new FormError(PATIENT_ID, IS_NOT_ALIVE));
        }
    }

    public void validateIfFacilityExists(List<FormError> formErrors, String facilityId) {
        if (facilityService.getFacilityByMotechId(facilityId) == null)
            formErrors.add(new FormError(FACILITY_ID, NOT_FOUND));
    }

    public void validateIfStaffExists(List<FormError> formErrors, final String staffId) {
        if (staffService.getUserById(staffId) == null) {
            formErrors.add(new FormError("staffId", NOT_FOUND));
        }
    }


}
