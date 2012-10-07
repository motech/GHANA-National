package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CWCEnrollmentForm implements FormWithHistoryInput {

    private String staffId;
    private String patientMotechId;
    private String serialNumber;
    private Date registrationDate;
    private Boolean addHistory;
    private FacilityForm facilityForm;
    private RegistrationToday registrationToday;
    private Date vitADate;
    private Date lastIPTiDate;
    private Date lastOPVDate;
    private Date bcgDate;
    private Date lastPentaDate;
    private Date measlesDate;
    private Date yfDate;
    private List<CwcCareHistory> careHistory;
    private Integer lastIPTi;
    private Integer lastOPV;
    private Integer lastPenta;
    private Date lastRotavirusDate;
    private Integer lastRotavirus;
    private Date lastPneumococcalDate;
    private Integer lastPneumococcal;
    private String lastVitaminA;
    private Integer lastMeasles;


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

    public List<CwcCareHistory> getCareHistory() {
        return careHistory;
    }

    public void setCareHistory(List<CwcCareHistory> careHistory) {
        this.careHistory = careHistory;
    }

    public Integer getLastIPTi() {
        return lastIPTi;
    }

    public void setLastIPTi(Integer lastIPTi) {
        this.lastIPTi = lastIPTi;
    }

    public Integer getLastOPV() {
        return lastOPV;
    }

    public void setLastOPV(Integer lastOPV) {
        this.lastOPV = lastOPV;
    }

    public Integer getLastPenta() {
        return lastPenta;
    }

    public void setLastPenta(Integer lastPenta) {
        this.lastPenta = lastPenta;
    }

    public Date getLastOPVDate() {
        return lastOPVDate;
    }

    public void setLastOPVDate(Date lastOPVDate) {
        this.lastOPVDate = lastOPVDate;
    }

    public Date getLastRotavirusDate() {
        return lastRotavirusDate;
    }

    public Integer getLastRotavirus() {
        return lastRotavirus;
    }

    public void setLastRotavirusDate(Date lastRotavirusDate) {
        this.lastRotavirusDate = lastRotavirusDate;
    }

    public void setLastRotavirus(Integer lastRotavirus) {
        this.lastRotavirus = lastRotavirus;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public RegistrationToday getRegistrationToday() {
        return registrationToday;
    }

    public void setRegistrationToday(RegistrationToday registrationToday) {
        this.registrationToday = registrationToday;
    }

    public Date getLastPneumococcalDate() {
        return lastPneumococcalDate;
    }

    public void setLastPneumococcalDate(Date lastPneumococcalDate) {
        this.lastPneumococcalDate = lastPneumococcalDate;
    }

    public Integer getLastPneumococcal() {
        return lastPneumococcal;
    }

    public void setLastPneumococcal(Integer lastPneumococcal) {
        this.lastPneumococcal = lastPneumococcal;
    }


    @Override
    public HashMap<String, Date> getHistoryDatesMap() {
        return new HashMap<String, Date>() {{
            put("lastPneumococcalDate", lastPneumococcalDate);
            put("lastIPTiDate", lastIPTiDate);
            put("lastOPVDate", lastOPVDate);
            put("lastPentaDate", lastPentaDate);
            put("lastRotavirusDate", lastRotavirusDate);
            put("bcgDate", bcgDate);
            put("yfDate", yfDate);
            put("vitADate", vitADate);
            put("measlesDate", measlesDate);
        }};
    }

    public String getLastVitaminA() {
        return lastVitaminA;
    }

    public void setLastVitaminA(String lastVitaminA) {
        this.lastVitaminA = lastVitaminA;
    }

    public Integer getLastMeasles() {
        return lastMeasles;
    }

    public void setLastMeasles(Integer lastMeasles) {
        this.lastMeasles = lastMeasles;
    }
}
