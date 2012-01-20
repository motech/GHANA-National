package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;

import java.util.Date;
import java.util.List;

public class ANCVO {

    private String staffId;
    private String facilityId;
    private String motechPatientId;
    private Date registrationDate;
    private RegistrationToday registrationToday;
    private String serialNumber;
    private Date estimatedDateOfDelivery;
    private Double height;
    private Integer gravida;
    private Integer parity;
    private Boolean addHistory;
    private Boolean deliveryDateConfirmed;
    private ANCCareHistoryVO ancCareHistoryVO;

    public ANCVO(String staffId, String facilityId, String motechPatientId, Date registrationDate, RegistrationToday registrationToday, String serialNumber,
                 Date estimatedDateOfDelivery, Double height, Integer gravida, Integer parity, Boolean addHistory, Boolean deliveryDateConfirmed,
                 List<ANCCareHistory> careHistory, String lastIPT, String lastTT, Date lastIPTDate, Date lastTTDate) {
        this.staffId = staffId;
        this.facilityId = facilityId;
        this.motechPatientId = motechPatientId;
        this.registrationDate = registrationDate;
        this.registrationToday = registrationToday;
        this.serialNumber = serialNumber;
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
        this.height = height;
        this.gravida = gravida;
        this.parity = parity;
        this.addHistory = addHistory;
        this.deliveryDateConfirmed = deliveryDateConfirmed;
        this.ancCareHistoryVO=new ANCCareHistoryVO(careHistory,lastIPT,lastTT,lastIPTDate,lastTTDate);
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getPatientMotechId() {
        return motechPatientId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public RegistrationToday getRegistrationToday() {
        return registrationToday;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Date getEstimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public Double getHeight() {
        return height;
    }

    public Integer getGravida() {
        return gravida;
    }

    public Integer getParity() {
        return parity;
    }

    public Boolean getAddHistory() {
        return addHistory;
    }

    public Boolean getDeliveryDateConfirmed() {
        return deliveryDateConfirmed;
    }

    public ANCCareHistoryVO getAncCareHistoryVO() {
        return ancCareHistoryVO;
    }
}
