package org.motechproject.ghana.national.bean;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;
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
import java.util.HashMap;
import java.util.List;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.MOTECH_ID_PATTERN;

public class CareHistoryForm extends FormBean implements FormWithHistoryInput {

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;

    @Required
    private Date date;

    @Required
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
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
    private Integer lastRotavirus;
    private Date lastPentaDate;
    private Integer lastMeasles;
    private Date measlesDate;
    private Date yellowFeverDate;
    private Integer lastIPTI;
    private Date lastIPTIDate;
    private String lastVitaminA;
    private Date lastVitaminADate;
    private Date lastRotavirusDate;
    private Integer lastPneumococcal;
    private Date lastPneumococcalDate;

    //ANC CARE OBSERVATIONS
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

    public Date getLastRotavirusDate() {
        return lastRotavirusDate;
    }

    public void setLastRotavirusDate(Date lastRotavirusDate) {
        this.lastRotavirusDate = lastRotavirusDate;
    }

    public Integer getLastRotavirus() {
        return lastRotavirus;
    }

    public void setLastRotavirus(Integer lastRotavirus) {
        this.lastRotavirus = lastRotavirus;
    }

    public Integer getLastPneumococcal() {
        return lastPneumococcal;
    }

    public void setLastPneumococcal(Integer lastPneumococcal) {
        this.lastPneumococcal = lastPneumococcal;
    }

    public Date getLastPneumococcalDate() {
        return lastPneumococcalDate;
    }

    public void setLastPneumococcalDate(Date lastPneumococcalDate) {
        this.lastPneumococcalDate = lastPneumococcalDate;
    }

    public String getLastVitaminA() {
        return lastVitaminA;
    }

    public void setLastVitaminA(String lastVitaminA) {
        this.lastVitaminA = lastVitaminA;
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
        ANCCareHistoryVO ancCareHistoryVO = new ANCCareHistoryVO(ancCareHistories.size() > 0, ancCareHistories, lastIPT, lastTT, lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis, lastMalaria, lastDiarrhea, lastPnuemonia, lastIPTDate, lastTTDate, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate);
        CWCCareHistoryVO cwcCareHistoryVO = new CWCCareHistoryVO(cwcCareHistories.size() > 0, cwcCareHistories, bcgDate, lastVitaminADate, lastVitaminA, measlesDate, lastMeasles, yellowFeverDate, lastPentaDate, lastPenta, lastOPVDate, lastOPV, lastIPTI, lastIPTIDate, lastRotavirus, lastRotavirusDate, lastPneumococcal, lastPneumococcalDate);

        return new CareHistoryVO(staffId, facilityId, motechId, date, ancCareHistoryVO, cwcCareHistoryVO);
    }

    public Integer getLastMeasles() {
        return lastMeasles;
    }

    public void setLastMeasles(Integer lastMeasles) {
        this.lastMeasles = lastMeasles;
    }

    @Override
    public String groupId() {
        return motechId;
    }

    @Override
    public HashMap<String, Date> getHistoryDatesMap() {
        return new HashMap<String, Date>() {{
            put("lastPneumococcalDate", lastPneumococcalDate);
            put("lastIPTiDate", lastIPTIDate);
            put("lastOPVDate", lastOPVDate);
            put("lastPentaDate", lastPentaDate);
            put("lastRotavirusDate", lastRotavirusDate);
            put("bcgDate", bcgDate);
            put("yfDate", yellowFeverDate);
            put("vitADate", lastVitaminADate);
            put("measlesDate", measlesDate);
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
