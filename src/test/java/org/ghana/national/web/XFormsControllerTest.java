package org.ghana.national.web;


import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class XFormsControllerTest {
    @Test
    public void shouldSerializeFormsInADirectory() throws IOException {
        ClassPathResource resource = new ClassPathResource("xforms/NurseQuery");
        File[] allFiles = resource.getFile().listFiles();
        List<String> files = new ArrayList<String>();
        for (File file : allFiles) {
            files.add(FileUtils.readFileToString(file));
        }



//        List<File> forms = Arrays.asList(directory.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".xml");
//            }
//        }));
//
//        serializer.serializeForms(output, forms, studyId, studyName, studyKey);
    }
}