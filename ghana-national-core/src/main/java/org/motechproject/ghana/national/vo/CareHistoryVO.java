package org.motechproject.ghana.national.vo;

import java.util.Date;

public class CareHistoryVO {

    private String staffId;
    private String facilityId;
    private String patientMotechId;
    private Date date;

    private ANCCareHistoryVO ancCareHistoryVO;
    private CWCCareHistoryVO cwcCareHistoryVO;

    public CareHistoryVO(String staffId, String facilityId, String patientMotechId, Date date,ANCCareHistoryVO ancCareHistoryVO, CWCCareHistoryVO cwcCareHistoryVO) {
        this.staffId = staffId;
        this.facilityId = facilityId;
        this.patientMotechId = patientMotechId;
        this.date = date;
        this.ancCareHistoryVO = ancCareHistoryVO;
        this.cwcCareHistoryVO = cwcCareHistoryVO;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getPatientMotechId() {
        return patientMotechId;
    }

    public Date getDate() {
        return date;
    }

    public ANCCareHistoryVO getAncCareHistoryVO() {
        return ancCareHistoryVO;
    }

    public CWCCareHistoryVO getCwcCareHistoryVO() {
        return cwcCareHistoryVO;
    }
}

