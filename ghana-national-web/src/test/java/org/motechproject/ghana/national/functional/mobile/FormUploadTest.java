package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class FormUploadTest {
    @Test
    public void shouldDownloadDefaultXFormsFromGhanaNational() {
        final HttpClient httpClient = new HttpClient();
        try {
            final PostMethod postMethod = new PostMethod("http://localhost:8080/ghana-national-web/formdownload");
            httpClient.executeMethod(postMethod);
            final InputStream responseStream = postMethod.getResponseBodyAsStream();
            EpihandyXformSerializer serializer = new EpihandyXformSerializer();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
