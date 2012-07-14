package org.motechproject.ghana.national.ivr;

import org.apache.tiles.ArrayStack;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;

public class TwiML {
    private List<Action> actions = new ArrayStack<Action>();

    public TwiML() {
    }

    public TwiML(String xml) throws ParserConfigurationException, IOException, SAXException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
        Node responseNode = document.getChildNodes().item(0);
        NodeList actionNodes = responseNode.getChildNodes();
        for (int i = 0; i < actionNodes.getLength(); i++) {
            Node actionNode = actionNodes.item(i);
            NamedNodeMap attributes = actionNode.getAttributes();

            if ("Play".equals(actionNode.getNodeName())) {
                actions.add(new Play(actionNode.getTextContent()));
            } else if ("Gather".equals(actionNode.getNodeName())) {
                actions.add(new Gather(attributes.getNamedItem("action").getTextContent()));
            } else if ("Dial".equals(actionNode.getNodeName())) {
                actions.add(new Dial(actionNode.getTextContent(), attributes.getNamedItem("callerId").getTextContent()));
            }
        }
    }

    public TwiML addAction(Action action) {
        actions.add(action);
        return this;
    }

    public List<Action> getActions() {
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwiML twiML = (TwiML) o;

        if (actions != null ? !actions.equals(twiML.actions) : twiML.actions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return actions != null ? actions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TwiML{" +
                "actions=" + actions +
                '}';
    }

    public Gather getGather() {
        for (Action action : actions) {
            if (action instanceof Gather)
                return (Gather) action;
        }
        return null;
    }

    public static interface Action {
    }

    public static class Dial implements Action {
        private String phoneNumber;

        private String callerId;

        public Dial() {
        }

        public Dial(String phoneNumber, String callerId) {
            this.phoneNumber = phoneNumber;
            this.callerId = callerId;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getCallerId() {
            return callerId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Dial dial = (Dial) o;

            if (callerId != null ? !callerId.equals(dial.callerId) : dial.callerId != null) return false;
            if (phoneNumber != null ? !phoneNumber.equals(dial.phoneNumber) : dial.phoneNumber != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = phoneNumber != null ? phoneNumber.hashCode() : 0;
            result = 31 * result + (callerId != null ? callerId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Dial{" +
                    "phoneNumber='" + phoneNumber + '\'' +
                    ", callerId='" + callerId + '\'' +
                    '}';
        }
    }

    public static class Gather implements Action {
        private String action;

        public Gather() {
        }

        public Gather(String action) {
            this.action = action;
        }

        public String getAction(String digitsToReturn) {
            return action + "&Digits=" + digitsToReturn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return action != null ? action.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Gather{" +
                    "action='" + action + '\'' +
                    '}';
        }
    }

    public static class Play implements Action {
        private String url;

        public Play(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Play play = (Play) o;

            if (url != null ? !url.equals(play.url) : play.url != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return url != null ? url.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Play{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "                        <Play>http://192.168.42.50:8080/ghana-national-web/resource/stream/EN/prompt_A.wav</Play>\n" +
                "                                    <Gather method=\"POST\" action=\"http://10.16.3.37:8080/ghana-national-web/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw\"></Gather>\n" +
                "                    <Gather method=\"POST\" action=\"http://10.16.3.37:8080/ghana-national-web/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw\"></Gather>\n" +
                "                    <Gather method=\"POST\" action=\"http://10.16.3.37:8080/ghana-national-web/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw\"></Gather>\n" +
                "                    <Gather method=\"POST\" action=\"http://10.16.3.37:8080/ghana-national-web/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw\"></Gather>\n" +
                "                    <Gather method=\"POST\" action=\"http://10.16.3.37:8080/ghana-national-web/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw\"></Gather>\n" +
                "             </Response>";
        TwiML twiML = new TwiML(xml);
        twiML.getActions();
    }
}
