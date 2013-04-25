package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.helper.FormWithHistoryInput;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ANCEnrollmentForm implements FormWithHistoryInput{
    private String staffId;
    private FacilityForm facilityForm;
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
    private List<ANCCareHistory> careHistory;
    private String lastIPT;
    private String lastTT;
    private Date lastTTDate;
    private Date lastIPTDate;

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




    public ANCEnrollmentForm() {
    }

    public Boolean getDeliveryDateConfirmed() {
        return deliveryDateConfirmed;
    }

    public void setDeliveryDateConfirmed(Boolean deliveryDateConfirmed) {
        this.deliveryDateConfirmed = deliveryDateConfirmed;
    }

    public ANCEnrollmentForm(String motechPatientId, String serialNumber, Date registrationDate) {
        this.motechPatientId = motechPatientId;
        this.serialNumber = serialNumber;
        this.registrationDate = registrationDate;
    }

    public ANCEnrollmentForm(String motechPatientId) {
        this.motechPatientId = motechPatientId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public FacilityForm getFacilityForm() {
        return facilityForm;
    }

    public void setFacilityForm(FacilityForm facilityForm) {
        this.facilityForm = facilityForm;
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

    public Boolean getAddHistory() {
        return addHistory;
    }

    public void setAddHistory(Boolean addHistory) {
        this.addHistory = addHistory;
    }

    public String getLastTT() {
        return lastTT;
    }

    public void setLastTT(String lastTT) {
        this.lastTT = lastTT;
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

    public RegistrationToday getRegistrationToday() {
        return registrationToday;
    }

    public void setRegistrationToday(RegistrationToday registrationToday) {
        this.registrationToday = registrationToday;
    }

    public String getMotechPatientId() {
        return motechPatientId;
    }

    public void setMotechPatientId(String motechPatientId) {
        this.motechPatientId = motechPatientId;
    }

    public Date getEstimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public void setEstimatedDateOfDelivery(Date estimatedDateOfDelivery) {
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
    }

    public List<ANCCareHistory> getCareHistory() {
        return careHistory;
    }

    public void setCareHistory(List<ANCCareHistory> careHistory) {
        this.careHistory = careHistory;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }


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

    public Date getLastMalariaDate() {
        return lastMalariaDate;
    }

    public void setLastMalariaDate(Date lastMalariaDate) {
        this.lastMalariaDate = lastMalariaDate;
    }

    public Date getLastSyphilisDate() {
        return lastSyphilisDate;
    }

    public void setLastSyphilisDate(Date lastSyphilisDate) {
        this.lastSyphilisDate = lastSyphilisDate;
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



    @Override
    public HashMap<String, Date> getHistoryDatesMap(){
        return new HashMap<String, Date>(){{
            put("lastIPTDate",lastIPTDate);
            put("lastTTDate",lastTTDate);
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
