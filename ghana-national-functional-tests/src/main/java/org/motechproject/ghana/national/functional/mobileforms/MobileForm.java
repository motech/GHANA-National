package org.motechproject.ghana.national.functional.mobileforms;

public class MobileForm {
    private String studyName;
    private String xmlTemplateName;

    public static MobileForm registerClientForm() {
        return new MobileForm("NurseDataEntry", "register-client-template.xml");
    }

    public static MobileForm editClientForm() {
        return new MobileForm("NurseDataEntry", "edit-client-template.xml");
    }

    public static MobileForm registerANCForm() {
        return new MobileForm("NurseDataEntry", "register-anc-template.xml");
    }

    public static MobileForm registerCWCForm() {
        return new MobileForm("NurseDataEntry", "register-cwc-template.xml");
    }

    public static MobileForm deliveryNotificationForm() {
        return new MobileForm("NurseDataEntry", "delivery-notification-template.xml");
    }

    public static MobileForm registerMobileMidwifeForm() {
        return new MobileForm("NurseDataEntry", "mobile-midwife-template.xml");
    }

    public static MobileForm ancVisitForm() {
        return new MobileForm("NurseDataEntry", "anc-visit-template.xml");
    }

    public MobileForm(String studyName, String xmlTemplateName) {
        this.studyName = studyName;
        this.xmlTemplateName = xmlTemplateName;
    }

    public String getStudyName() {
        return studyName;
    }

    public String getXmlTemplateName() {
        return xmlTemplateName;
    }

    public static MobileForm registerCWCVisitForm() {
        return new MobileForm("NurseDataEntry", "cwc-visit-template.xml");
    }
}
