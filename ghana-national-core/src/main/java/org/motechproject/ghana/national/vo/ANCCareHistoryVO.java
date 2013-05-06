package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.ANCCareHistory;

import java.util.Date;
import java.util.List;

public class ANCCareHistoryVO {

    Boolean addCareHistory;
    List<ANCCareHistory> careHistory;
    String lastIPT;
    String lastTT;
    private String lastHbLevels;
    private String lastMotherVitaminA;
    private String lastIronOrFolate;
    private String lastSyphilis;
    private String lastMalaria;
    private String lastDiarrhea;
    private String lastPnuemonia;

    Date lastTTDate;
    Date lastIPTDate;
    private Date lastHbDate;
    private Date lastMotherVitaminADate;
    private Date lastIronOrFolateDate;
    private Date lastSyphilisDate;
    private Date lastMalariaDate;
    private Date lastDiarrheaDate;
    private Date lastPnuemoniaDate;

    public ANCCareHistoryVO(Boolean addCareHistory, List<ANCCareHistory> careHistory, String lastIPT, String lastTT, String lastHbLevels, String lastMotherVitaminA,
            String lastIronOrFolate, String lastSyphilis, String lastMalaria, String lastDiarrhea,String lastPnuemonia, Date lastIPTDate, Date lastTTDate,
            Date lastHbDate, Date lastMotherVitaminADate, Date lastIronOrFolateDate, Date lastSyphilisDate, Date lastMalariaDate, Date lastDiarrheaDate, Date lastPnuemoniaDate) {
        this.addCareHistory = addCareHistory;
        this.careHistory=careHistory;
        this.lastIPT=lastIPT;
        this.lastTT=lastTT;
        this.lastHbLevels= lastHbLevels;
        this.lastMotherVitaminA=lastMotherVitaminA;
        this.lastIronOrFolate=lastIronOrFolate;
        this.lastSyphilis=lastSyphilis;
        this.lastMalaria=lastMalaria;
        this.lastDiarrhea=lastDiarrhea;
        this.lastPnuemonia=lastPnuemonia;
        this.lastIPTDate=lastIPTDate;
        this.lastTTDate=lastTTDate;
        this.lastHbDate=lastHbDate;
        this.lastMotherVitaminADate=lastMotherVitaminADate;
        this.lastIronOrFolateDate=lastIronOrFolateDate;
        this.lastSyphilisDate=lastSyphilisDate;
        this.lastMalariaDate=lastMalariaDate;
        this.lastDiarrheaDate=lastDiarrheaDate;
        this.lastPnuemoniaDate=lastPnuemoniaDate;
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

    public String getLastMalaria() {
        return lastMalaria;
    }

    public String getLastDiarrhea() {
        return lastDiarrhea;
    }

    public String getLastPnuemonia() {
        return lastPnuemonia;
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

    public Date getLastMalariaDate() {
        return lastMalariaDate;
    }

    public Date getLastDiarrheaDate() {
        return lastDiarrheaDate;
    }

    public Date getLastPnuemoniaDate() {
        return lastPnuemoniaDate;
    }

    public Boolean getAddCareHistory() {
        return addCareHistory;
    }
}