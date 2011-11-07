package org.motechproject.ghana.national.bean;

import org.motechproject.mobileforms.api.domain.FormBean;

public class GeneralQueryForm extends FormBean {
    private String chpsid;
    private String sender;
    private String facilityid;
    private String formname;
    private String formtype;

    public String getChpsid() {
        return chpsid;
    }

    public void setChpsid(String chpsid) {
        this.chpsid = chpsid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFacilityid() {
        return facilityid;
    }

    public void setFacilityid(String facilityid) {
        this.facilityid = facilityid;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public String getFormtype() {
        return formtype;
    }

    public void setFormtype(String formtype) {
        this.formtype = formtype;
    }
}
