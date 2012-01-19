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

    public MobileMidWifeIncludeForm setConsent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public Boolean isEnrolledForProgram() {
        return enroll != null ? enroll : false;
    }

    public MobileMidWifeIncludeForm setEnroll(Boolean enrollForProgram) {
        this.enroll = enrollForProgram;
        return this;
    }

    public MobileMidwifeEnrollment fillEnrollment(MobileMidwifeEnrollment enrollment) {
        return super.fillEnrollment(enrollment).setConsent(getConsent());
    }
}
