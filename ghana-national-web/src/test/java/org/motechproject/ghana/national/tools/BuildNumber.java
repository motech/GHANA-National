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
    private static final String BUILD_NUMBER_PROPERTY = "BUILD_NUMBER";
    private static final String BUILD_NUMBER_PLACE_HOLDER_VALUE = "${build_number}";

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || buildNumber().equals(BUILD_NUMBER_PLACE_HOLDER_VALUE)) {
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
        String buildNumber = buildNumber();
        return String.format("<![CDATA[<!-- %s -->]]>\n</div>", buildNumber);
    }

    private static String buildNumber() {
        return System.getProperty(BUILD_NUMBER_PROPERTY, BUILD_NUMBER_PLACE_HOLDER_VALUE);
    }
}
