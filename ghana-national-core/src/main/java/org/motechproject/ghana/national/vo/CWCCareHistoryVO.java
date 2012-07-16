package org.motechproject.ghana.national.vo;

import org.motechproject.ghana.national.domain.CwcCareHistory;

import java.util.Date;
import java.util.List;

public class CWCCareHistoryVO {

    private Boolean addCareHistory;
    private List<CwcCareHistory> cwcCareHistories;
    private Date bcgDate;
    private Date vitADate;
    private Date measlesDate;
    private Date yfDate;
    private Date lastPentaDate;
    private Integer lastPenta;
    private Date lastRotavirusDate;
    private Integer lastRotavirus;
    private Date lastOPVDate;
    private Integer lastOPV;
    private Date lastIPTiDate;
    private Integer lastIPTi;
    public CWCCareHistoryVO(Boolean addCareHistory, List<CwcCareHistory> careHistories, Date bcgDate, Date vitADate, Date measlesDate, Date yfDate, Date lastPentaDate,
                            Integer lastPenta, Date lastOPVDate, Integer lastOPV, Integer lastIPTi, Date lastIPTiDate, Integer lastRotavirus, Date lastRotavirusDate) {
        this.addCareHistory = addCareHistory;
        this.cwcCareHistories=careHistories;
        this.bcgDate=bcgDate;
        this.vitADate=vitADate;
        this.measlesDate=measlesDate;
        this.yfDate=yfDate;
        this.lastPentaDate=lastPentaDate;
        this.lastPenta=lastPenta;
        this.lastOPVDate=lastOPVDate;
        this.lastOPV=lastOPV;
        this.lastIPTiDate=lastIPTiDate;
        this.lastIPTi=lastIPTi;
        this.lastRotavirus=lastRotavirus;
        this.lastRotavirusDate=lastRotavirusDate;
    }

    public CWCCareHistoryVO() {
    }

    public List<CwcCareHistory> getCwcCareHistories() {
        return cwcCareHistories;
    }

    public Date getBcgDate() {
        return bcgDate;
    }

    public Date getVitADate() {
        return vitADate;
    }

    public Date getMeaslesDate() {
        return measlesDate;
    }

    public Date getYfDate() {
        return yfDate;
    }

    public Date getLastPentaDate() {
        return lastPentaDate;
    }

    public Integer getLastPenta() {
        return lastPenta;
    }

    public Date getLastOPVDate() {
        return lastOPVDate;
    }

    public Integer getLastOPV() {
        return lastOPV;
    }

    public Date getLastIPTiDate() {
        return lastIPTiDate;
    }

    public Integer getLastIPTi() {
        return lastIPTi;
    }

    public Boolean getAddCareHistory() {
        return addCareHistory;
    }

    public Integer getLastRotavirus() {
        return lastRotavirus;
    }

    public Date getLastRotavirusDate() {
        return lastRotavirusDate;
    }

}