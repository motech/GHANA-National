package org.motechproject.ghana.national.ivr;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;

public class IVRInboundCallTest {

    private VerboiceStub verboiceStub;
    private TestAppServer testAppServer;

    @Before
    public void setUp() throws Exception {
        testAppServer = new TestAppServer();
        verboiceStub = new VerboiceStub(testAppServer);
    }

    @Test
    public void shouldHandleInboundCall() {
        testAppServer.startApplication();
        String response = verboiceStub.handleIncomingCall();

        String selectLanguagePrompt = AudioPrompts.LANGUAGE_PROMPT.value() + ".wav";
        TwiML expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(selectLanguagePrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=Lw")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather());//.addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, response);
        String languageChoice = "1";
        response = verboiceStub.handle(response, languageChoice);

        String welcomeMessagePrompt = AudioPrompts.OPTIONS_PROMPT.value() + ".wav";
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(welcomeMessagePrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=LzE")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather());//.addAction(new TwiML.Gather());
        verboiceStub.expect(expectedActions, response);

        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);

        String motechIdPrompt = AudioPrompts.MOTECH_ID_PROMPT.value() + ".wav";
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(motechIdPrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=LzE")));
        verboiceStub.expect(expectedActions, response);

        String invalidMotechIdPrompt = AudioPrompts.INVALID_MOTECH_ID_PROMPT.value() + ".wav";
        response = verboiceStub.handle(response, "18374873");
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN")))
                .addAction(new TwiML.Gather(testAppServer.path("/verboice/ivr?type=verboice&amp;ln=en&amp;tree=mm&amp;trP=LzEvMS8xODM3NDg3Mw")));
        verboiceStub.expect(expectedActions, response);

        String noMessagesPrompt = AudioPrompts.NO_MESSAGE_IN_OUTBOX.value() + ".wav";
        response = verboiceStub.handle(response, "1234568");
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(noMessagesPrompt, "EN")));
        verboiceStub.expect(expectedActions, response);

        System.out.println(response);


    }

    public static void main(String[] args) {
        System.out.println(new String(Base64.decodeBase64("LzEvMQ".getBytes())));

    }
}
