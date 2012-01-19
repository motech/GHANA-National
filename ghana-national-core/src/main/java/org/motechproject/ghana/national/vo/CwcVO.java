package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.CwcCareHistory;

import java.util.Date;
import java.util.List;

public class CwcVO {

    private String staffId;
    private String facilityId;
    private Date registrationDate;
    private String patientMotechId;
    private List<CwcCareHistory> cwcCareHistories;
    private Date bcgDate;
    private Date vitADate;
    private Date measlesDate;
    private Date yfDate;
    private Date lastPentaDate;
    private Integer lastPenta;
    private Date lastOPVDate;
    private Integer lastOPV;
    private Date lastIPTiDate;
    private Integer lastIPTi;
    private String serialNumber;

    public CwcVO(String staffId, String facilityId, Date registrationDate, String patientMotechId, List<CwcCareHistory> cwcCareHistories, Date bcgDate, Date vitADate,
                 Date measlesDate, Date yfDate, Date lastPentaDate, Integer lastPenta, Date lastOPVDate, Integer lastOPV, Date lastIPTiDate, Integer lastIPTi, String serialNumber) {
        this.staffId = staffId;
        this.facilityId = facilityId;
        this.registrationDate = registrationDate;
        this.patientMotechId = patientMotechId;
        this.cwcCareHistories = cwcCareHistories;
        this.bcgDate = bcgDate;
        this.vitADate = vitADate;
        this.measlesDate = measlesDate;
        this.yfDate = yfDate;
        this.lastPentaDate = lastPentaDate;
        this.lastPenta = lastPenta;
        this.lastOPVDate = lastOPVDate;
        this.lastOPV = lastOPV;
        this.lastIPTiDate = lastIPTiDate;
        this.lastIPTi = lastIPTi;
        this.serialNumber = serialNumber;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public String getPatientMotechId() {
        return patientMotechId;
    }

    public Date getBcgDate() {
        return bcgDate;
    }

    public Date getVitADate() {
        return vitADate;
    }

    public Date getMeaslesDate() {
        return measlesDate;
    }

    public Date getYfDate() {
        return yfDate;
    }

    public Date getLastPentaDate() {
        return lastPentaDate;
    }

    public Integer getLastPenta() {
        return lastPenta;
    }

    public Date getLastOPVDate() {
        return lastOPVDate;
    }

    public Integer getLastOPV() {
        return lastOPV;
    }

    public Date getLastIPTiDate() {
        return lastIPTiDate;
    }

    public Integer getLastIPTi() {
        return lastIPTi;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public List<CwcCareHistory> getCwcCareHistories() {
        return cwcCareHistories;
    }

    public void setCwcCareHistories(List<CwcCareHistory> cwcCareHistories) {
        this.cwcCareHistories = cwcCareHistories;
    }
}
