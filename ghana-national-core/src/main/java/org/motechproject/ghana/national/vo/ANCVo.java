package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.RegistrationToday;

import java.util.Date;

public class ANCVo {
    private String motechPatientId;
    private String serialNumber;
    private Date estimatedDateOfDelivery;
    private String facilityId;
    private Integer gravida;
    private Integer parity;
    private Float height;
    private String lastIPT;
    private String lastTT;
    private Date lastTTDate;
    private Date lastIPTDate;
    private RegistrationToday registrationToday;
    private Date registrationDate;

    public ANCVo(String motechPatientId, Date registrationDate, String serialNumber, Integer gravida, Integer parity, Float  height, Date estimatedDateOfDelivery) {
        this.motechPatientId = motechPatientId;
        this.registrationDate = registrationDate;
        this.serialNumber = serialNumber;
        this.gravida = gravida;
        this.parity = parity;
        this.height = height;
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
    }

    public String getMotechPatientId() {
        return motechPatientId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }


    public Date getEstimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public Integer getGravida() {
        return gravida;
    }

    public Integer getParity() {
        return parity;
    }

    public Float getHeight() {
        return height;
    }

    public String getLastIPT() {
        return lastIPT;
    }

    public String getLastTT() {
        return lastTT;
    }

    public Date getLastTTDate() {
        return lastTTDate;
    }

    public Date getLastIPTDate() {
        return lastIPTDate;
    }

    public RegistrationToday getRegistrationToday() {
        return registrationToday;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void addCareHistory(ANCCareHistoryVo ancCareHistoryVo) {

    }
}
