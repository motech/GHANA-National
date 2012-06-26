package org.motechproject.ghana.national.ivr;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;

public class IVRInboundCallIT {

    private static VerboiceStub verboiceStub;
    private static TestAppServer testAppServer;
    private static Patient patientWithMM;

    @BeforeClass
    public static void setUp() throws Exception {
        testAppServer = new TestAppServer();
        verboiceStub = new VerboiceStub(testAppServer);
        testAppServer.startApplication();

        patientWithMM = testAppServer.createPatient("patientWithMM", "lastName");
        testAppServer.registerForMobileMidwife(patientWithMM);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        testAppServer.shutdownApplication();
    }

    @Test
    public void shouldHandleInboundCallForPatientWithMMRegistrationAndNoMessagesInInbox() {
        String response = verboiceStub.handleIncomingCall();

        String selectLanguagePrompt = fileName(AudioPrompts.LANGUAGE_PROMPT);
        TwiML expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(selectLanguagePrompt, "EN")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, response);
        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);

        String reasonForCallPrompt = fileName(AudioPrompts.REASON_FOR_CALL_PROMPT);
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(reasonForCallPrompt, "EN")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather());
        verboiceStub.expect(expectedActions, response);

        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);

        String motechIdPrompt = fileName(AudioPrompts.MOTECH_ID_PROMPT);
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(motechIdPrompt, "EN")))
                .addAction(new TwiML.Gather());
        verboiceStub.expect(expectedActions, response);

        String invalidMotechIdPrompt = fileName(AudioPrompts.NO_MESSAGE_IN_OUTBOX);
        response = verboiceStub.handle(response, patientWithMM.getMotechId());
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN")));
        verboiceStub.expect(expectedActions, response);
    }

    @Test
    @Ignore
    public void shouldHandleInboundCallForPatientWithMMRegistrationAndWithMessagesInInbox() {
        DateTime now = DateTime.now();
        testAppServer.addCareMessageToOutbox(patientWithMM.getMotechId(), AudioPrompts.TT_DUE.value(), now);
        testAppServer.addCareMessageToOutbox(patientWithMM.getMotechId(), AudioPrompts.IPT_DUE.value(), now.minus(Period.days(1)));
        testAppServer.addCareMessageToOutbox(patientWithMM.getMotechId(), AudioPrompts.PNC_MOTHER_DUE.value(), now.plus(Period.days(1)));
        testAppServer.addAppointmentMessageToOutbox(patientWithMM.getMotechId(), AudioPrompts.ANC_DUE.value());
        testAppServer.addMobileMidwifeMessageToOutbox(patientWithMM.getMotechId(), MobileMidwifeAudioClips.PREGNANCY_WEEK_7);

        String response = verboiceStub.handleIncomingCall();
        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);
        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);

        // first message
        response = verboiceStub.handle(response, patientWithMM.getMotechId());
        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.IPT_DUE), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.TT_DUE), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.PNC_MOTHER_DUE), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.ANC_DUE), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(0)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(0)), "EN")))
                .addAction(new TwiML.Gather()).addAction(new TwiML.Gather()).addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, response);

        // repeat first message
        String repeatOption = "1";
            String repeatResponse = verboiceStub.handle(response, repeatOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(0)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(0)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, repeatResponse);


        // play second message from first message menu
        String playSecondMessageOption = "2";
        String secondMessageResponse = verboiceStub.handle(repeatResponse, playSecondMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(1)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(1)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, secondMessageResponse);

        // repeat second message
        String repeatSecondMessageOption = "1";
        String secondMessageRepeatResponse = verboiceStub.handle(secondMessageResponse, repeatSecondMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(1)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(1)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, secondMessageRepeatResponse);

        // play third message from second message menu

        String playThirdMessageOption = "2";
        String thirdMessageResponse = verboiceStub.handle(secondMessageRepeatResponse, playThirdMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(2)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(2)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, thirdMessageResponse);

        // repeat third message

        String repeatThirdMessageOption = "1";
        String thirdMessageRepeatResponse = verboiceStub.handle(thirdMessageResponse, repeatThirdMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(2)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(2)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, thirdMessageRepeatResponse);

        // play second message from third message menu

        String secondMessageOption = "1";
        secondMessageResponse = verboiceStub.handle(thirdMessageRepeatResponse, secondMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(1)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(1)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, secondMessageResponse);

        // play third message from first message menu
        playThirdMessageOption = "3";
        response = verboiceStub.handle(repeatResponse, playThirdMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(2)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(2)), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, response);










    }

    private String fileName(AudioPrompts audioPrompt) {
        return audioPrompt.value() + ".wav";
    }

    private String fileName(String clipName) {
        return clipName + ".wav";
    }

}
