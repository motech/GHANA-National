package org.ghana.national.web;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.motechproject.proto.MFormsProtocolHandlerImpl;
import org.openxdata.proto.SubmissionContext;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context implements SubmissionContext {
    private Logger log = Logger.getLogger("SubmissionContext");

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private byte action;

    public Context(DataInputStream inputStream, DataOutputStream outputStream, byte action) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.action = action;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public String getLocale() {
        return "en";
    }

    public byte getAction() {
        return action;
    }

    public List<Object[]> getUsers() {
        return new ArrayList<Object[]>();
    }

    public List<Object[]> getStudies() {
        return new ArrayList<Object[]>() {{
            Object[] objects = getStudyForms(0).toArray();
            add(new Object[]{1, "FieldAgentForms"});
            add(new Object[]{2, "NurseDataEntry"});
        }};

    }

    public String getStudyName(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getStudyKey(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getStudyForms(int i) {
        log.warn("baz");
        return new ArrayList<String>(this.getXForms().values());
    }

    public Map<Integer, String> getXForms() {
        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        log.warn("foo");
        try {
            ClassPathResource resource = new ClassPathResource("xforms/NurseQuery");
            File[] allFiles;
            allFiles = resource.getFile().listFiles();
            for (int i = 0, allFilesLength = allFiles.length; i < allFilesLength; i++) {
                File file = allFiles[i];
                hashMap.put(i, FileUtils.readFileToString(file));
            }

        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        log.warn("bar");

        return hashMap;
    }

    public void setUploadResult(List<String> strings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String setUploadResult(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
