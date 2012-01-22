package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.mobileforms.api.validator.annotations.Required;

public abstract class MobileMidWifeIncludeForm extends AbstractMobileMidWifeForm {

    @Required
    private Boolean enroll;

    private Boolean consent;

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public Boolean isEnrolledForMobileMidwifeProgram() {
        return enroll != null ? enroll : false;
    }

    public void setEnroll(Boolean enrollForProgram) {
        this.enroll = enrollForProgram;
    }

    public MobileMidwifeEnrollment fillEnrollment(MobileMidwifeEnrollment enrollment) {
        return super.fillEnrollment(enrollment).setConsent(getConsent());
    }
}
