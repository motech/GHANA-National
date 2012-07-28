package org.motechproject.ghana.national.ivr;

import org.apache.tiles.ArrayStack;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwiML {
    private List<Action> actions = new ArrayStack<Action>();

    public TwiML() {
    }

    public TwiML(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        Node responseNode = document.getChildNodes().item(0);
        NodeList actionNodes = responseNode.getChildNodes();
        for (int i = 0; i < actionNodes.getLength(); i++) {
            Node actionNode = actionNodes.item(i);
            NamedNodeMap attributes = actionNode.getAttributes();

            if ("Play".equals(actionNode.getNodeName())) {
                actions.add(new Play(actionNode.getTextContent()));
            } else if ("Gather".equals(actionNode.getNodeName())) {
                Node timeout = attributes.getNamedItem("timeout");
                Node finishOnKey = attributes.getNamedItem("finishOnKey");
                Gather gather = new Gather(attributes.getNamedItem("action").getTextContent(),
                        timeout != null ? timeout.getTextContent(): null,
                        finishOnKey != null ? finishOnKey.getTextContent(): null);
                actions.add(gather);
                for (int j = 0; j < actionNode.getChildNodes().getLength(); j++) {
                    Node childNode = actionNode.getChildNodes().item(j);
                    if ("Play".equals(childNode.getNodeName())) {
                        gather.addPrompt(new Play(childNode.getTextContent()));
                    }
                }
            } else if ("Dial".equals(actionNode.getNodeName())) {
                actions.add(new Dial());
            } else if ("Redirect".equals(actionNode.getNodeName())) {
                actions.add(new Redirect(actionNode.getTextContent()));
            } else if ("Hangup".equals(actionNode.getNodeName())){
                actions.add(new Exit());
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

    public Redirect getRedirect() {
        for (Action action : actions) {
            if (action instanceof Redirect)
                return (Redirect) action;
        }
        return null;
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

    public static class Redirect implements Action {
        private String url;

        public Redirect(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Redirect redirect = (Redirect) o;

            if (url != null ? !url.equals(redirect.url) : redirect.url != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return url != null ? url.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Redirect{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    public static class Dial implements Action {
        public Dial() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }
    }

    public static class Gather implements Action {
        private String action;
        private String timeoutInSec;
        private String finishOnKey;
        private List<Play> prompts = new ArrayList<Play>();

        public Gather() {
        }

        public Gather(String action, String timeoutInSec, String finishOnKey) {
            this.action = action;
            this.timeoutInSec = timeoutInSec;
            this.finishOnKey = finishOnKey;
        }

        public Gather addPrompt(Play play) {
            this.prompts.add(play);
            return this;
        }

        public String getAction(String digitsToReturn) {
            return action + "&Digits=" + digitsToReturn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Gather gather = (Gather) o;

            if (finishOnKey != null ? !finishOnKey.equals(gather.finishOnKey) : gather.finishOnKey != null)
                return false;
            if (prompts != null ? !prompts.equals(gather.prompts) : gather.prompts != null) return false;
            if (timeoutInSec != null ? !timeoutInSec.equals(gather.timeoutInSec) : gather.timeoutInSec != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = timeoutInSec != null ? timeoutInSec.hashCode() : 0;
            result = 31 * result + (finishOnKey != null ? finishOnKey.hashCode() : 0);
            result = 31 * result + (prompts != null ? prompts.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Gather{" +
                    "action='" + action + '\'' +
                    ", timeoutInSec='" + timeoutInSec + '\'' +
                    ", finishOnKey='" + finishOnKey + '\'' +
                    ", prompts=" + prompts +
                    '}';
        }
    }

    public static class Exit implements Action {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return true;
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

}
