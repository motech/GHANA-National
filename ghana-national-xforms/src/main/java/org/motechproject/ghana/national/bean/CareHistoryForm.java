package org.motechproject.ghana.national.bean;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.ghana.national.vo.CareHistoryVO;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CareHistoryForm extends FormBean {

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;

    @Required
    private Date date;

    @Required
    @RegEx(pattern = Constants.MOTECH_ID_PATTERN) @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private String addHistory;
    private String lastIPT;
    private Date lastIPTDate;
    private String lastTT;
    private Date lastTTDate;
    private Date bcgDate;
    private Integer lastOPV;
    private Date lastOPVDate;
    private Integer lastPenta;
    private Date lastPentaDate;
    private Date measlesDate;
    private Date yellowFeverDate;
    private Integer lastIPTI;
    private Date lastIPTIDate;
    private Date lastVitaminADate;

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

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getAddHistory() {
        return addHistory;
    }

    public void setAddHistory(String addHistory) {
        this.addHistory = addHistory;
    }

    public String getLastIPT() {
        return lastIPT;
    }

    public void setLastIPT(String lastIPT) {
        this.lastIPT = lastIPT;
    }

    public Date getLastIPTDate() {
        return lastIPTDate;
    }

    public void setLastIPTDate(Date lastIPTDate) {
        this.lastIPTDate = lastIPTDate;
    }

    public String getLastTT() {
        return lastTT;
    }

    public void setLastTT(String lastTT) {
        this.lastTT = lastTT;
    }

    public Date getLastTTDate() {
        return lastTTDate;
    }

    public void setLastTTDate(Date lastTTDate) {
        this.lastTTDate = lastTTDate;
    }

    public Date getBcgDate() {
        return bcgDate;
    }

    public void setBcgDate(Date bcgDate) {
        this.bcgDate = bcgDate;
    }

    public Integer getLastOPV() {
        return lastOPV;
    }

    public void setLastOPV(Integer lastOPV) {
        this.lastOPV = lastOPV;
    }

    public Date getLastOPVDate() {
        return lastOPVDate;
    }

    public void setLastOPVDate(Date lastOPVDate) {
        this.lastOPVDate = lastOPVDate;
    }

    public Integer getLastPenta() {
        return lastPenta;
    }

    public void setLastPenta(Integer lastPenta) {
        this.lastPenta = lastPenta;
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

    public Date getYellowFeverDate() {
        return yellowFeverDate;
    }

    public void setYellowFeverDate(Date yellowFeverDate) {
        this.yellowFeverDate = yellowFeverDate;
    }

    public Integer getLastIPTI() {
        return lastIPTI;
    }

    public void setLastIPTI(Integer lastIPTI) {
        this.lastIPTI = lastIPTI;
    }

    public Date getLastIPTIDate() {
        return lastIPTIDate;
    }

    public void setLastIPTIDate(Date lastIPTIDate) {
        this.lastIPTIDate = lastIPTIDate;
    }

    public Date getLastVitaminADate() {
        return lastVitaminADate;
    }

    public void setLastVitaminADate(Date lastVitaminADate) {
        this.lastVitaminADate = lastVitaminADate;
    }

    public List<ANCCareHistory> getANCCareHistories() {
        String[] selectedCareHistories = StringUtils.isNotEmpty(addHistory) ? addHistory.split(" ") : new String[]{};
        final List<String> ancCareHistories = new ArrayList<String>();
        for (ANCCareHistory ancCareHistory : ANCCareHistory.values()) {
            ancCareHistories.add(ancCareHistory.name());
        }

        List<ANCCareHistory> filteredCareHistories = new ArrayList<ANCCareHistory>();
        for (String history : selectedCareHistories) {
            if (ancCareHistories.contains(history)) {
                filteredCareHistories.add(ANCCareHistory.valueOf(history));
            }
        }
        return filteredCareHistories;
    }

    public List<CwcCareHistory> getCWCCareHistories() {
        String[] selectedCareHistories = StringUtils.isNotEmpty(addHistory) ? addHistory.split(" ") : new String[]{};
        final List<String> cwcCareHistories = new ArrayList<String>();
        for (CwcCareHistory cwcCareHistory : CwcCareHistory.values()) {
            cwcCareHistories.add(cwcCareHistory.name());
        }

        List<CwcCareHistory> filteredCareHistories = new ArrayList<CwcCareHistory>();
        for (String history : selectedCareHistories) {
            if (cwcCareHistories.contains(history)) {
                filteredCareHistories.add(CwcCareHistory.valueOf(history));
            }
        }
        return filteredCareHistories;
    }

    public CareHistoryVO careHistoryVO(String facilityId) {
        List<ANCCareHistory> ancCareHistories = getANCCareHistories();
        List<CwcCareHistory> cwcCareHistories = getCWCCareHistories();
        ANCCareHistoryVO ancCareHistoryVO = new ANCCareHistoryVO(ancCareHistories.size() > 0, ancCareHistories, lastIPT, lastTT, lastIPTDate, lastTTDate);
        CWCCareHistoryVO cwcCareHistoryVO = new CWCCareHistoryVO(cwcCareHistories.size() > 0, cwcCareHistories, bcgDate, lastVitaminADate, measlesDate, yellowFeverDate, lastPentaDate, lastPenta, lastOPVDate, lastOPV, lastIPTI, lastIPTDate);

        return new CareHistoryVO(staffId, facilityId, motechId, date, ancCareHistoryVO, cwcCareHistoryVO);
    }
}
