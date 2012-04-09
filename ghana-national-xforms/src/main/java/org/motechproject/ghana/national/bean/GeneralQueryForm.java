package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.GeneralQueryType;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.PHONE_NO_PATTERN;

public class GeneralQueryForm extends FormBean {
    @Required
    private String staffId;
    @Required
    private String facilityId;
    @Required
    @RegEx(pattern = PHONE_NO_PATTERN)
    private String responsePhoneNumber;
    @Required
    private GeneralQueryType queryType;

    public String getStaffId() {
        return this.staffId;
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

    public String getResponsePhoneNumber() {
        return responsePhoneNumber;
    }

    public void setResponsePhoneNumber(String responsePhoneNumber) {
        this.responsePhoneNumber = responsePhoneNumber;
    }

    public GeneralQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(GeneralQueryType queryType) {
        this.queryType = queryType;
    }
}
