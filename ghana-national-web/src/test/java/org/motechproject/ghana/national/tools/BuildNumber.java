package org.motechproject.ghana.national.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Inserts the Hudson build number as a comment in the footer jsp
 */
public class BuildNumber {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            return;
        }
        File file = new File("ghana-national-web/src/main/webapp/WEB-INF/views/footer.jspx");
        if (!file.exists()) return;
        FileWriter writer = null;
        try {
            if (args[0].equals("pre")) {
                writer = new FileWriter(file, true);
                insert(writer);
            } else {
                //content has to be stored before creating the writer.
                final List<String> content = FileUtils.readLines(file);
                writer = new FileWriter(file, false);
                remove(writer, content);
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void remove(FileWriter writer, List<String> strings) throws IOException {
        for (int i = 0; i < strings.size() - 1; i++) {
            writer.write(strings.get(i) + "\n");
        }
    }

    private static void insert(FileWriter writer) throws IOException {
        String buildNumber = System.getProperty("BUILD_NUMBER");
        if (StringUtils.isEmpty(buildNumber)) {
            buildNumber = "dev";
        }
        writer.append("\n<!--").append(buildNumber).append("-->");
    }
}
