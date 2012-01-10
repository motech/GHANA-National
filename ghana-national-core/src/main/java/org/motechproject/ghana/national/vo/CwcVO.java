package org.motechproject.ghana.national.vo;

import java.util.Date;

public class CwcVO {

    private String staffId;
    private String facilityId;
    private Date registrationDate;
    private String patientMotechId;
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

    public CwcVO(String staffId, String facilityId, Date registrationDate, String patientMotechId, Date bcgDate, Date vitADate,
                 Date measlesDate, Date yfDate, Date lastPentaDate, Integer lastPenta, Date lastOPVDate, Integer lastOPV, Date lastIPTiDate, Integer lastIPTi) {
        this.staffId = staffId;
        this.facilityId = facilityId;
        this.registrationDate = registrationDate;
        this.patientMotechId = patientMotechId;
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
}
