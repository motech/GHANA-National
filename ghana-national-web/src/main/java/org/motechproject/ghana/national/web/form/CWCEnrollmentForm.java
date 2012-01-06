package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.CwcCareHistory;

import java.util.Date;

public class CWCEnrollmentForm {

    private String patientMotechId;
    private String serialNumber;
    private Date registrationDate;
    private Boolean addHistory;
    private FacilityForm facilityForm;
    private Date vitADate;
    private Date lastIPTiDate;
    private Date lastOPVDate;
    private Date bcgDate;
    private Date lastPentaDate;
    private Date measlesDate;
    private Date yfDate;
    private CwcCareHistory careHistory;
    private String lastIPTi;
    private String lastOPV;
    private String lastPenta;


    public FacilityForm getFacilityForm() {
        return facilityForm;
    }

    public void setFacilityForm(FacilityForm facilityForm) {
        this.facilityForm = facilityForm;
    }

    public String getPatientMotechId() {
        return patientMotechId;
    }

    public void setPatientMotechId(String patientMotechId) {
        this.patientMotechId = patientMotechId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Boolean getAddHistory() {
        return addHistory;
    }

    public void setAddHistory(Boolean addHistory) {
        this.addHistory = addHistory;
    }

    public Date getVitADate() {
        return vitADate;
    }

    public void setVitADate(Date vitADate) {
        this.vitADate = vitADate;
    }

    public Date getLastIPTiDate() {
        return lastIPTiDate;
    }

    public void setLastIPTiDate(Date lastIPTiDate) {
        this.lastIPTiDate = lastIPTiDate;
    }

    public Date getBcgDate() {
        return bcgDate;
    }

    public void setBcgDate(Date bcgDate) {
        this.bcgDate = bcgDate;
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

    public Date getYfDate() {
        return yfDate;
    }

    public void setYfDate(Date yfDate) {
        this.yfDate = yfDate;
    }

    public CwcCareHistory getCareHistory() {
        return careHistory;
    }

    public void setCareHistory(CwcCareHistory careHistory) {
        this.careHistory = careHistory;
    }

    public String getLastIPTi() {
        return lastIPTi;
    }

    public void setLastIPTi(String lastIPTi) {
        this.lastIPTi = lastIPTi;
    }

    public String getLastOPV() {
        return lastOPV;
    }

    public void setLastOPV(String lastOPV) {
        this.lastOPV = lastOPV;
    }

    public String getLastPenta() {
        return lastPenta;
    }

    public void setLastPenta(String lastPenta) {
        this.lastPenta = lastPenta;
    }

    public Date getLastOPVDate() {
        return lastOPVDate;
    }

    public void setLastOPVDate(Date lastOPVDate) {
        this.lastOPVDate = lastOPVDate;
    }
}
