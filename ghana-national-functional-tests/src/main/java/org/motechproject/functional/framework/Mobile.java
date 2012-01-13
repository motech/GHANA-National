package org.motechproject.functional.framework;

import org.motechproject.MotechException;
import org.motechproject.functional.mobileforms.MobileForm;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Mobile {
    public XformHttpClient.XformResponse upload(MobileForm mobileForm, Map<String, String> templateData) {
        try {
            return XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                    mobileForm.getStudyName(), XformHttpClient.XFormParser.parse(mobileForm.getXmlTemplateName(), templateData));
        } catch (Exception e) {
            throw new MotechException("Encountered error while uploading mobile form", e);
        }

    }
}
