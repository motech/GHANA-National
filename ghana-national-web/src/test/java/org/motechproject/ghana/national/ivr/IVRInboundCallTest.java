package org.motechproject.ghana.national.ivr;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.fail;

public class IVRInboundCallTest {

    private VerboiceStub verboiceStub;
    private TestAppServer testAppServer;

    @Before
    public void setUp() throws Exception {
        testAppServer = new TestAppServer();
        verboiceStub = new VerboiceStub(testAppServer);
    }

    @Test
    public void shouldHandleInboundCall(){
        testAppServer.startApplication();
        String response = verboiceStub.handleIncomingCall();

        String selectLanguagePrompt = AudioPrompts.LANGUAGE_PROMPT.value() + ".wav";
        TwiML expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(selectLanguagePrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, response);
        String languageChoice = "1";
        response = verboiceStub.handle(response, languageChoice);

        String welcomeMessagePrompt = AudioPrompts.OPTIONS_PROMPT.value() + ".wav";
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(welcomeMessagePrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=LzE")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather());
        verboiceStub.expect(expectedActions, response);

        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);

        String motechIdPrompt = AudioPrompts.MOTECHID_PROMPT.value() + ".wav";
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(motechIdPrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=LzE")));
        verboiceStub.expect(expectedActions, response);

        String motechId = "18374873";
        response = verboiceStub.handle(response, motechId);
        System.out.println(response);


    }
}
