package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.ghana.national.domain.FormUploadLog;
import org.motechproject.ghana.national.repository.AllFormUploadLogs;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormUploadLoggerTest {

    @Mock
    private AllFormUploadLogs allFormUploadLogs;

    @InjectMocks
    private FormUploadLogger formUploadLogger=new FormUploadLogger();


    @Before
    public void setUp() {
        initMocks(this);

    }

    @Test
    public void shouldLogTheFormsUploaded() {
        Map<String, Object> parameters = new HashMap<>();

        String xmlContent1 = "<xml>Content1</xml>";
        String xmlContent2 = "<xml>Content2</xml>";
        List<FormBean> formBeans= new ArrayList<>();
        formBeans.add(new TestFormBean(xmlContent1));
        formBeans.add(new TestFormBean(xmlContent2));
        FormBeanGroup formBeanGroup = new FormBeanGroup(formBeans);

        parameters.put("formBeanGroup",formBeanGroup);
        MotechEvent event = new MotechEvent("subject", parameters);
        formUploadLogger.handle(event);

        ArgumentCaptor<FormUploadLog> formUploadLogCaptor = ArgumentCaptor.forClass(FormUploadLog.class);
        verify(allFormUploadLogs, times(2)).add(formUploadLogCaptor.capture());
        assertThat(formUploadLogCaptor.getAllValues().get(0).getFormContent(),is(xmlContent1));
        assertThat(formUploadLogCaptor.getAllValues().get(1).getFormContent(),is(xmlContent2));
    }

    public class TestFormBean extends FormBean{

        public TestFormBean(String xmlContent){
            super(xmlContent);
        }

        @Override
        public String groupId() {
            return groupId();
        }
    }
}
