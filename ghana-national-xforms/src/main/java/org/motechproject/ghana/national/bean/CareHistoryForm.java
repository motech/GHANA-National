package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

public class CareHistoryForm extends FormBean {
    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;

    @Required
    private Date date;

    @Required
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private String addHistory;
    private String lastIPT;
    private Date lastIPTDate;
    private String lastTT;
    private Date lastTTDate;
    private Date bcgDate;
    private String lastOPV;
    private Date lastOPVDate;
    private String lastPenta;
    private Date lastPentaDate;
    private Date measlesDate;
    private Date yellowFeverDate;
    private String lastIPTI;
    private Date lastIPTIDate;
    private Date lastVitaminADate;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getAddHistory() {
        return addHistory;
    }

    public void setAddHistory(String addHistory) {
        this.addHistory = addHistory;
    }

    public String getLastIPT() {
        return lastIPT;
    }

    public void setLastIPT(String lastIPT) {
        this.lastIPT = lastIPT;
    }

    public Date getLastIPTDate() {
        return lastIPTDate;
    }

    public void setLastIPTDate(Date lastIPTDate) {
        this.lastIPTDate = lastIPTDate;
    }

    public String getLastTT() {
        return lastTT;
    }

    public void setLastTT(String lastTT) {
        this.lastTT = lastTT;
    }

    public Date getLastTTDate() {
        return lastTTDate;
    }

    public void setLastTTDate(Date lastTTDate) {
        this.lastTTDate = lastTTDate;
    }

    public Date getBcgDate() {
        return bcgDate;
    }

    public void setBcgDate(Date bcgDate) {
        this.bcgDate = bcgDate;
    }

    public String getLastOPV() {
        return lastOPV;
    }

    public void setLastOPV(String lastOPV) {
        this.lastOPV = lastOPV;
    }

    public Date getLastOPVDate() {
        return lastOPVDate;
    }

    public void setLastOPVDate(Date lastOPVDate) {
        this.lastOPVDate = lastOPVDate;
    }

    public String getLastPenta() {
        return lastPenta;
    }

    public void setLastPenta(String lastPenta) {
        this.lastPenta = lastPenta;
    }

    public Date getLastPentaDate() {
        return lastPentaDate;
    }

    public void setLastPentaDate(Date lastPentaDate) {
        this.lastPentaDate = lastPentaDate;
    }

    public Date getMeaslesDate() {
        return measlesDate;
    }

    public void setMeaslesDate(Date measlesDate) {
        this.measlesDate = measlesDate;
    }

    public Date getYellowFeverDate() {
        return yellowFeverDate;
    }

    public void setYellowFeverDate(Date yellowFeverDate) {
        this.yellowFeverDate = yellowFeverDate;
    }

    public String getLastIPTI() {
        return lastIPTI;
    }

    public void setLastIPTI(String lastIPTI) {
        this.lastIPTI = lastIPTI;
    }

    public Date getLastIPTIDate() {
        return lastIPTIDate;
    }

    public void setLastIPTIDate(Date lastIPTIDate) {
        this.lastIPTIDate = lastIPTIDate;
    }

    public Date getLastVitaminADate() {
        return lastVitaminADate;
    }

    public void setLastVitaminADate(Date lastVitaminADate) {
        this.lastVitaminADate = lastVitaminADate;
    }
}
