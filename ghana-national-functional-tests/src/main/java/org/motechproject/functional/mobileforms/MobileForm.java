package org.motechproject.functional.mobileforms;

public class MobileForm {
    private String studyName;
    private String xmlTemplateName;

    public static MobileForm registerClientForm(){
        return new MobileForm("NurseDataEntry", "register-client-template.xml");
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
