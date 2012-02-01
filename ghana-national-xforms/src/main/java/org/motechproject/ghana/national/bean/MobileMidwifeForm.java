package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

public class MobileMidwifeForm extends AbstractMobileMidWifeForm {

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;
    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;
    @Required
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String patientId;

    @Required
    private Boolean consent;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public MobileMidwifeEnrollment createMobileMidwifeEnrollment() {
        MobileMidwifeEnrollment enrollment = fillEnrollment(MobileMidwifeEnrollment.newEnrollment());
        return enrollment.setStaffId(getStaffId()).setFacilityId(getFacilityId()).setPatientId(getPatientId())
                .setConsent(getConsent());
    }
}
