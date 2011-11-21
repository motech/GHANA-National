package org.motechproject.ghana.national.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
            final List<String> content = FileUtils.readLines(file);
            writer = new FileWriter(file, false);
            IOUtils.writeLines(content.subList(0, content.size() - 2), IOUtils.LINE_SEPARATOR, writer);
            IOUtils.write(args[0].equals("pre") ? insert() : remove(), writer);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private static String remove() throws IOException {
        return "<![CDATA[<!-- ${build_number} -->]]>\n</div>";
    }

    private static String insert() throws IOException {
        String buildNumber = System.getProperty("BUILD_NUMBER");
        if (StringUtils.isEmpty(buildNumber)) {
            buildNumber = "${build_number}";
        }
       return "<![CDATA[<!-- " + buildNumber + " -->]]>\n</div>";
    }
}
