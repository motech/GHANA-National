package org.motechproject.ghana.national.functional.framework;

import org.motechproject.MotechException;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Mobile {

    public XformHttpClient.XformResponse upload(MobileForm mobileForm, Map<String, String> templateData) {
        try {
            final XformHttpClient.XformResponse response = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                    mobileForm.getStudyName(), XformHttpClient.XFormParser.parse(mobileForm.getXmlTemplateName(), templateData));
            return response;

        } catch (Exception e) {
            throw new MotechException("Encountered error while uploading mobile form", e);
        }
    }

    public void fillForms(String studyName, FormBuilder formBuilder, DataOutputStream dataOutputStream) throws IOException, ParseException {
        XformHttpClient.serializeForms(studyName, formBuilder.forms(), dataOutputStream);
    }


    public static class FormBuilder {
        private List<String> forms = new ArrayList<String>();

        public FormBuilder() {}

        public FormBuilder addForm(String templateName, Map<String, String> templateData) throws Exception {
            forms.add(XformHttpClient.XFormParser.parse(templateName, templateData));
            return this;
        }

        public String[] forms() {
            return forms.toArray(new String[forms.size()]);
        }
    }


}
