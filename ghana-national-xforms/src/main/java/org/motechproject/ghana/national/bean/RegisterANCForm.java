package org.motechproject.ghana.national.bean;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterANCForm extends MobileMidWifeIncludeForm {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String staffId;

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;

    private Date date;

    @Required
    private RegistrationToday regDateToday;

    @Required
    @RegEx(pattern = Constants.MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private Date estDeliveryDate;

    @Required
    private Boolean deliveryDateConfirmed;

    @Required
    private Boolean addHistory;

    private Date lastIPTDate;

    private Date lastTTDate;

    private String lastIPT;

    private String lastTT;

    @Required
    private Double height;

    @Required
    private Integer gravida;

    @Required
    private Integer parity;

    @Required
    private String ancRegNumber;

    @RegEx(pattern = "0[0-9]{9}")
    private String regPhone;

    private String addCareHistory;


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

    public RegistrationToday getRegDateToday() {
        return regDateToday;
    }

    public void setRegDateToday(RegistrationToday regDateToday) {
        this.regDateToday = regDateToday;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public Date getEstDeliveryDate() {
        return estDeliveryDate;
    }

    public void setEstDeliveryDate(Date estDeliveryDate) {
        this.estDeliveryDate = estDeliveryDate;
    }

    public Boolean getDeliveryDateConfirmed() {
        return deliveryDateConfirmed;
    }

    public void setDeliveryDateConfirmed(Boolean deliveryDateConfirmed) {
        this.deliveryDateConfirmed = deliveryDateConfirmed;
    }

    public Boolean getAddHistory() {
        return addHistory;
    }

    public void setAddHistory(Boolean addHistory) {
        this.addHistory = addHistory;
    }

    public Date getLastIPTDate() {
        return lastIPTDate;
    }

    public void setLastIPTDate(Date lastIPTDate) {
        this.lastIPTDate = lastIPTDate;
    }

    public Date getLastTTDate() {
        return lastTTDate;
    }

    public void setLastTTDate(Date lastTTDate) {
        this.lastTTDate = lastTTDate;
    }

    public String getLastIPT() {
        return lastIPT;
    }

    public void setLastIPT(String lastIPT) {
        this.lastIPT = lastIPT;
    }

    public String getLastTT() {
        return lastTT;
    }

    public void setLastTT(String lastTT) {
        this.lastTT = lastTT;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getGravida() {
        return gravida;
    }

    public void setGravida(Integer gravida) {
        this.gravida = gravida;
    }

    public Integer getParity() {
        return parity;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

    public String getAncRegNumber() {
        return ancRegNumber;
    }

    public void setAncRegNumber(String ancRegNumber) {
        this.ancRegNumber = ancRegNumber;
    }

    public String getRegPhone() {
        return regPhone;
    }

    public void setRegPhone(String regPhone) {
        this.regPhone = regPhone;
    }

    public String getAddCareHistory() {
        return addCareHistory;
    }

    public void setAddCareHistory(String addCareHistory) {
        this.addCareHistory = addCareHistory;
    }

    public MobileMidwifeEnrollment createMobileMidwifeEnrollment() {
        if (isEnrolledForMobileMidwifeProgram()) {
            MobileMidwifeEnrollment enrollment = fillEnrollment(new MobileMidwifeEnrollment());
            return enrollment.setStaffId(getStaffId()).setFacilityId(getFacilityId()).setPatientId(getMotechId());
        }
        return null;
    }

    public List<ANCCareHistory> getANCCareHistories() {
        List<ANCCareHistory> ancCareHistories = new ArrayList<ANCCareHistory>();
        if (StringUtils.isNotEmpty(addCareHistory)) {
            for (String value : addCareHistory.split(" ")) {
                ancCareHistories.add(ANCCareHistory.valueOf(value));
            }
        }
        return ancCareHistories;
    }
}

