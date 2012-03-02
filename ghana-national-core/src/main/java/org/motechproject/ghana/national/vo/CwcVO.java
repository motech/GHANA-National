package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.CwcCareHistory;

import java.util.Date;
import java.util.List;

public class CwcVO {

    private String staffId;
    private String facilityId;
    private Date registrationDate;
    private String patientMotechId;
    private String serialNumber;
    private CWCCareHistoryVO cwcCareHistoryVO;

    public CwcVO(String staffId, String facilityId, Date registrationDate, String patientMotechId, List<CwcCareHistory> careHistories, Date bcgDate,
                 Date vitADate, Date measlesDate, Date yfDate, Date lastPentaDate, Integer lastPenta, Date lastOPVDate, Integer lastOPV, Date lastIPTiDate,
                 Integer lastIPTi, String serialNumber, Boolean addCareHistory) {
        this.staffId = staffId;
        this.facilityId = facilityId;
        this.registrationDate = registrationDate;
        this.patientMotechId = patientMotechId;
        this.serialNumber = serialNumber;
        this.cwcCareHistoryVO = new CWCCareHistoryVO(addCareHistory, careHistories, bcgDate, vitADate, measlesDate, yfDate, lastPentaDate, lastPenta, lastOPVDate,
                lastOPV, lastIPTi, lastIPTiDate);
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public CWCCareHistoryVO getCWCCareHistoryVO() {
        return cwcCareHistoryVO;
    }
}
