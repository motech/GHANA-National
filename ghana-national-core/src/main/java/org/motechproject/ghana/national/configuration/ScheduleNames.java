package org.motechproject.ghana.national.configuration;

public enum ScheduleNames {
    ANC_DELIVERY("Delivery","Delivery"),
    ANC_IPT_VACCINE("ANCIPTVaccine","IPTp"),
    CWC_MEASLES_VACCINE("CWCMeaslesVaccine","Measles"),
    CWC_IPT_VACCINE("CWCIPTVaccine","IPTi"),
    CWC_BCG("CWCBCGVaccine","BCG"),
    CWC_YELLOW_FEVER("CWCYellowFeverVaccine","YF"),
    CWC_PENTA("CWCPentaVaccine","Penta"),
    CWC_ROTAVIRUS("CWCRotavirus","Rotavirus"),
    CWC_PNEUMOCOCCAL("CWCPneumococcal","Pneumococcal"),
    TT_VACCINATION("TTVaccine","TT"),
    CWC_OPV_0("CWCOPV-0","OPV0"),
    CWC_OPV_OTHERS("CWC_OPV_OTHERS","OPV1,OPV2,OPV3"),
    PNC_CHILD_1("PNC-CHILD-1","PNC-CHILD-1"),
    PNC_CHILD_2("PNC-CHILD-2","PNC-CHILD-2"),
    PNC_CHILD_3("PNC-CHILD-3","PNC-CHILD-3"),
    PNC_MOTHER_1("PNC-MOTHER-1","PNC-MOTHER-1"),
    PNC_MOTHER_2("PNC-MOTHER-2","PNC-MOTHER-2"),
    PNC_MOTHER_3("PNC-MOTHER-3","PNC-MOTHER-3");

    private String name;
    private String friendlyName;

    ScheduleNames(String name,String friendlyName) {
        this.name = name;
        this.friendlyName=friendlyName;
    }

    public String getName() {
        return name;
    }

    public String getFriendlyName(){
        return friendlyName;
    }
}
