package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.ANCCareHistory;

import java.util.Date;
import java.util.List;

public class ANCCareHistoryVO {

    Boolean addCareHistory;
    List<ANCCareHistory> careHistory;
    private String lastIPT;
    private String lastTT;
    private String lastHbLevels;
    private String lastMotherVitaminA;
    private String lastIronOrFolate;
    private String lastSyphilis;

    private Date lastTTDate;
    private Date lastIPTDate;
    private Date lastHbDate;
    private Date lastMotherVitaminADate;
    private Date lastIronOrFolateDate;
    private Date lastSyphilisDate;


    public ANCCareHistoryVO(Boolean addCareHistory, List<ANCCareHistory> careHistory, String lastIPT, String lastTT, String lastHbLevels, String lastMotherVitaminA,
            String lastIronOrFolate, String lastSyphilis, Date lastIPTDate, Date lastTTDate, Date lastHbDate, Date lastMotherVitaminADate, Date lastIronOrFolateDate, Date lastSyphilisDate) {
        this.addCareHistory = addCareHistory;
        this.careHistory=careHistory;
        this.lastIPT=lastIPT;
        this.lastTT=lastTT;
        this.lastHbLevels= lastHbLevels;
        this.lastMotherVitaminA=lastMotherVitaminA;
        this.lastIronOrFolate=lastIronOrFolate;
        this.lastSyphilis=lastSyphilis;
        this.lastIPTDate=lastIPTDate;
        this.lastTTDate=lastTTDate;
        this.lastHbDate=lastHbDate;
        this.lastMotherVitaminADate=lastMotherVitaminADate;
        this.lastIronOrFolateDate=lastIronOrFolateDate;
        this.lastSyphilisDate=lastSyphilisDate;
    }

    public List<ANCCareHistory> getCareHistory() {
        return careHistory;
    }

    public String getLastIPT() {
        return lastIPT;
    }

    public String getLastTT() {
        return lastTT;
    }

    public Date getLastIPTDate() {
        return lastIPTDate;
    }

    public Date getLastTTDate() {
        return lastTTDate;
    }


    public String getLastHbLevels() {
        return lastHbLevels;
    }

    public String getLastMotherVitaminA() {
        return lastMotherVitaminA;
    }

    public String getLastIronOrFolate() {
        return lastIronOrFolate;
    }

    public String getLastSyphilis() {
        return lastSyphilis;
    }

    public Date getLastHbDate() {
        return lastHbDate;
    }

    public Date getLastMotherVitaminADate() {
        return lastMotherVitaminADate;
    }

    public Date getLastIronOrFolateDate() {
        return lastIronOrFolateDate;
    }

    public Date getLastSyphilisDate() {
        return lastSyphilisDate;
    }

    public Boolean getAddCareHistory() {
        return addCareHistory;
    }
}