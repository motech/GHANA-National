package org.motechproject.ghana.national.ivr;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.MotechException;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class VerboiceStub {
    private static final String VERBOICE_URL = "/verboice/ivr";
    private final HttpClient client;
    private TestAppServer testAppServer;

    public VerboiceStub(TestAppServer testAppServer) {
        this.testAppServer = testAppServer;
        client = new DefaultHttpClient();
    }

    public String handleIncomingCall() {
        String callbackUrl = testAppServer.path(VERBOICE_URL + "?type=verboice&ln=en&tree=mm&trP=Lw");
        return hitApp(callbackUrl);
    }

    public String handle(String xml, String digitsToReturnForDTMF) {
        TwiML actualActions = null;
        try {
            actualActions = new TwiML(xml);
            TwiML.Gather gather = actualActions.getGather();
            if(gather != null)
                return hitApp(gather.getAction(digitsToReturnForDTMF));
        } catch (Exception e) {
            throw new MotechException("Unable to process TWIML, " + xml, e);
        }
        return null;
    }

    private String hitApp(String url) {
        try {
            return client.execute(new HttpGet(url), new BasicResponseHandler());
        } catch (IOException e) {
            throw new MotechException("Unable to hit app, " + url, e);
        }
    }

    public void expect(TwiML expectedActions, String xml) {
        TwiML actualActions = null;
        try {
            actualActions = new TwiML(xml);
        } catch (Exception e) {
            fail("Not a valid twiml, " + xml);
        }
        assertTrue(xml, actualActions.equals(expectedActions));
    }
}
