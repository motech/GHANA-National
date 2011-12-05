package org.motechproject.ghana.national.functional.mobile;

import org.fcitmuk.epihandy.midp.db.FormDataStore;
import org.fcitmuk.epihandy.midp.db.StudyStore;
import org.fcitmuk.epihandy.midp.model.Model;
import org.fcitmuk.epihandy.midp.transport.FormUpload;
import org.junit.Ignore;
import org.junit.Test;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Ignore
public class FormUploadTest {
    @Test
    public void shouldDownloadDefaultXFormsFromGhanaNational() {
        try {
            final HttpConnection httpConnection = (HttpConnection) Connector.open("http://localhost:8080/ghana-national-web/formupload");
            httpConnection.setRequestMethod(HttpConnection.POST);
            httpConnection.setRequestProperty("Content-Type", "application/octet-stream");
            httpConnection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.0");
            httpConnection.setRequestProperty("Content-Language", "en-US");

            DataOutputStream dataOutputStream = httpConnection.openDataOutputStream();
            dataOutputStream.writeUTF("motech");
            dataOutputStream.writeUTF("ghs");
            dataOutputStream.writeUTF("");
            dataOutputStream.writeUTF("");
            dataOutputStream.writeByte(2);
            final StudyStore studyStore = new StudyStore();
            final FormDataStore formDataStore = new FormDataStore();
//            formDataStore.saveFormData()
            FormUpload formUpload = new FormUpload(new Model(studyStore, formDataStore));
            formUpload.write(dataOutputStream);

            if (httpConnection.getResponseCode() == HttpConnection.HTTP_OK) {
                System.out.println("success");
                final DataInputStream dataInputStream = httpConnection.openDataInputStream();
                assertResponse(dataInputStream);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void assertResponse(DataInputStream dataInputStream) {

    }
}
