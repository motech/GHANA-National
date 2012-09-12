package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ghana.national.domain.FormUploadLog;
import org.motechproject.ghana.national.repository.AllFormUploadLogs;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormUploadLogger {

    public static final String FORM_LOGGING_SUBJECT = "handle.xforms.group.logging";
    @Autowired
    private AllFormUploadLogs allFormUploadLogs;

    @MotechListener(subjects = FORM_LOGGING_SUBJECT)
    public void handle(MotechEvent event){
        FormBeanGroup formBeanGroup = (FormBeanGroup) event.getParameters().get("formBeanGroup");
        for (FormBean formBean : formBeanGroup.getFormBeans()) {
            FormUploadLog formUploadLog = new FormUploadLog(DateTime.now(), formBean.getXmlContent());
            allFormUploadLogs.add(formUploadLog);
        }
    }
}
