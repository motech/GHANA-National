package org.motechproject.ghana.national.bean;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.MOTECH_ID_PATTERN;
import static org.motechproject.ghana.national.FormFieldRegExPatterns.NUMERIC_OR_NOTAPPLICABLE_PATTERN;
import static org.motechproject.util.DateUtil.newDateTime;

public class RegisterANCForm extends MobileMidWifeIncludeForm implements FormWithHistoryInput{

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
    @RegEx(pattern = MOTECH_ID_PATTERN)
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

    private String addCareHistory;

    //NEW ANC CARE HISTORY
    private String lastHbLevels;
    private String lastMotherVitaminA;
    private String lastIronOrFolate;
    private String lastSyphilis;
    private String lastMalaria;
    private String lastDiarrhea;
    private String lastPnuemonia;
    private Date lastHbDate;
    private Date lastMotherVitaminADate;
    private Date lastIronOrFolateDate;
    private Date lastSyphilisDate;
    private Date lastMalariaDate;
    private Date lastDiarrheaDate;
    private Date lastPnuemoniaDate;


    public String getLastHbLevels() {
        return lastHbLevels;
    }

    public void setLastHbLevels(String lastHbLevels) {
        this.lastHbLevels = lastHbLevels;
    }

    public String getLastMotherVitaminA() {
        return lastMotherVitaminA;
    }

    public void setLastMotherVitaminA(String lastMotherVitaminA) {
        this.lastMotherVitaminA = lastMotherVitaminA;
    }

    public String getLastIronOrFolate() {
        return lastIronOrFolate;
    }

    public void setLastIronOrFolate(String lastIronOrFolate) {
        this.lastIronOrFolate = lastIronOrFolate;
    }

    public String getLastSyphilis() {
        return lastSyphilis;
    }

    public void setLastSyphilis(String lastSyphilis) {
        this.lastSyphilis = lastSyphilis;
    }

    public String getLastMalaria() {
        return lastMalaria;
    }

    public void setLastMalaria(String lastMalaria) {
        this.lastMalaria = lastMalaria;
    }

    public String getLastDiarrhea() {
        return lastDiarrhea;
    }

    public void setLastDiarrhea(String lastDiarrhea) {
        this.lastDiarrhea = lastDiarrhea;
    }

    public String getLastPnuemonia() {
        return lastPnuemonia;
    }

    public void setLastPnuemonia(String lastPnuemonia) {
        this.lastPnuemonia = lastPnuemonia;
    }

    public Date getLastHbDate() {
        return lastHbDate;
    }

    public void setLastHbDate(Date lastHbDate) {
        this.lastHbDate = lastHbDate;
    }

    public Date getLastMotherVitaminADate() {
        return lastMotherVitaminADate;
    }

    public void setLastMotherVitaminADate(Date lastMotherVitaminADate) {
        this.lastMotherVitaminADate = lastMotherVitaminADate;
    }

    public Date getLastIronOrFolateDate() {
        return lastIronOrFolateDate;
    }

    public void setLastIronOrFolateDate(Date lastIronOrFolateDate) {
        this.lastIronOrFolateDate = lastIronOrFolateDate;
    }

    public Date getLastSyphilisDate() {
        return lastSyphilisDate;
    }

    public void setLastSyphilisDate(Date lastSyphilisDate) {
        this.lastSyphilisDate = lastSyphilisDate;
    }

    public Date getLastMalariaDate() {
        return lastMalariaDate;
    }

    public void setLastMalariaDate(Date lastMalariaDate) {
        this.lastMalariaDate = lastMalariaDate;
    }

    public Date getLastDiarrheaDate() {
        return lastDiarrheaDate;
    }

    public void setLastDiarrheaDate(Date lastDiarrheaDate) {
        this.lastDiarrheaDate = lastDiarrheaDate;
    }

    public Date getLastPnuemoniaDate() {
        return lastPnuemoniaDate;
    }

    public void setLastPnuemoniaDate(Date lastPnuemoniaDate) {
        this.lastPnuemoniaDate = lastPnuemoniaDate;
    }


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

    public String getAddCareHistory() {
        return addCareHistory;
    }

    public void setAddCareHistory(String addCareHistory) {
        this.addCareHistory = addCareHistory;
    }

    public MobileMidwifeEnrollment createMobileMidwifeEnrollment() {
        if (isEnrolledForMobileMidwifeProgram()) {
            MobileMidwifeEnrollment enrollment = fillEnrollment(new MobileMidwifeEnrollment(newDateTime(getRegistrationDate())));
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

    public Date getRegistrationDate() {
         return getDate() == null ? DateUtil.today().toDate() : getDate();
    }

    @Override
    public String groupId() {
        return motechId;
    }

    @Override
    public HashMap<String, Date> getHistoryDatesMap() {
        return new HashMap<String, Date>() {{
            put("lastIPTDate", lastIPTDate);
            put("lastTTDate", lastTTDate);
            put("lastHbDate",lastHbDate);
            put("lastMotherVitaminADate",lastMotherVitaminADate);
            put("lastIronOrFolateDate",lastIronOrFolateDate);
            put("lastSyphilisDate",lastSyphilisDate);
            put("lastMalariaDate",lastMalariaDate);
            put("lastDiarrheaDate",lastDiarrheaDate);
            put("lastPnuemoniaDate",lastPnuemoniaDate);
        }};
    }
}

