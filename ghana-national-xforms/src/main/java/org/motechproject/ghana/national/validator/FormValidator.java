package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public static final String FACILITY_ID = "facilityId";
    public static final String STAFF_ID = "staffId";

    public List<FormError> validateIfPatientExistsAndIsAlive(String motechId, final String patientIdAttribute) {
        final Patient patient = patientService.getPatientByMotechId(motechId);
        if (patient == null) {
            return new ArrayList<FormError>() {{
                add(new FormError(patientIdAttribute, NOT_FOUND));
            }};
        } else if (patient.getMrsPatient().getPerson().isDead()) {
            return new ArrayList<FormError>() {{
                add(new FormError(patientIdAttribute, IS_NOT_ALIVE));
            }};
        }
        return new ArrayList<FormError>();
    }

    public List<FormError> validateIfPatientIsAliveAndIsAChild(String motechId, final String patientIdAttribute) {
        final Patient patient = patientService.getPatientByMotechId(motechId);
        if (patient == null) {
            return new ArrayList<FormError>() {{
                add(new FormError(patientIdAttribute, NOT_FOUND));
            }};
        } else if (patient.getMrsPatient().getPerson().isDead()) {
            return new ArrayList<FormError>() {{
                add(new FormError(patientIdAttribute, IS_NOT_ALIVE));
            }};
        } else if (!isChild(patient)) {
            return new ArrayList<FormError>() {{
                add(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.CHILD_AGE_MORE_ERR_MSG));
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

    public List<FormError> validateIfPatientIsFemale(String patientId, final String attributeName) {
        final Patient patient = patientService.getPatientByMotechId(patientId);
        if (patient.getMrsPatient().getPerson().getGender().equals(Constants.PATIENT_GENDER_MALE)) {
            return new ArrayList<FormError>() {{
                add(new FormError(attributeName, Constants.GENDER_ERROR_MSG));
            }};
        }
        return new ArrayList<FormError>();
    }

    public List<FormError> validateIfPatientIsAChild(String motechId) {
        Patient patient = patientService.getPatientByMotechId(motechId);
        if (!isChild(patient)) {
            return new ArrayList<FormError>() {{
                add(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.CHILD_AGE_MORE_ERR_MSG));
            }};
        }
        return new ArrayList<FormError>();
    }

    public List<FormError> validateIfPatientIsNotAChild(String motechId) {
        Patient patient = patientService.getPatientByMotechId(motechId);
        if (isChild(patient)) {
            return new ArrayList<FormError>() {{
                add(new FormError(Constants.CHILD_AGE_PARAMETER, Constants.AGE_LESS_ERR_MSG));
            }};
        }
        return new ArrayList<FormError>();
    }

    protected boolean isAgeGreaterThan(String motechId, int ageInYears) {
        return motechId != null && patientService.getAgeOfPatientByMotechId(motechId) >= ageInYears;
    }

    private boolean isChild(Patient patient) {
        return patient.getMrsPatient().getPerson().getAge() <= 5;
    }
}
