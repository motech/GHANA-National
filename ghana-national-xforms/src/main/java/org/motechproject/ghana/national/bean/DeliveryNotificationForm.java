package org.motechproject.ghana.national.bean;

import org.joda.time.DateTime;
import org.motechproject.mobileforms.api.domain.FormBean;

public class DeliveryNotificationForm extends FormBean {
    private String staffId;
    private String motechId;
    private String facilityId;
    private DateTime datetime;

    public DateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(DateTime datetime) {
        this.datetime = datetime;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
