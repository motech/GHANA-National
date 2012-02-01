package org.motechproject.functional.mobileforms;

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

    public static MobileForm careHistoryForm() {
        return new MobileForm("NurseDataEntry", "care-history-template.xml");
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
}
