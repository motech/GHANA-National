package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;

public class XFormParser {
    public static String parse(String templateName, Map<String, String> data) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(new File(XFormParser.class.getClassLoader().getResource(templateName).toURI()));

        for (Map.Entry<String, String> entry : data.entrySet()) {
            final NodeList nodeList = document.getElementsByTagName(entry.getKey());
            if (nodeList == null || nodeList.getLength() == 0)
                continue;
            final Node node = nodeList.item(0);
            node.setTextContent(entry.getValue());
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.getBuffer().toString();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
