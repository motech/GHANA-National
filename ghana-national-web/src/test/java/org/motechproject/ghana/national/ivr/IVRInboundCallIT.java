package org.motechproject.ghana.national.ivr;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class IVRInboundCallIT {

    private static VerboiceStub verboiceStub;
    private static TestAppServer testAppServer;
    private static Patient patientWithMM;
    public static final String INBOUND_DECISION_TREE_NAME = "InboundDecisionTree";

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
    public void shouldConnectToCallCenterIfUserOptedForItWhileSelectingAction() {
        String response = verboiceStub.handleIncomingCall();
        String englishLanguageOption = "1";
        response = verboiceStub.handle(response, englishLanguageOption);

        String callCenterPhoneNumber = "0111111111";
        TwiML expectedActions = new TwiML().addAction(new TwiML.Dial(callCenterPhoneNumber, callCenterPhoneNumber));

        String connectToCallCenterOption = "0";
        String responseForConnectToCallCenterOption = verboiceStub.handle(response, connectToCallCenterOption);
        verboiceStub.expect(expectedActions, responseForConnectToCallCenterOption);

        connectToCallCenterOption = "2";
        responseForConnectToCallCenterOption = verboiceStub.handle(response, connectToCallCenterOption);
        verboiceStub.expect(expectedActions, responseForConnectToCallCenterOption);

        connectToCallCenterOption = "*";
        responseForConnectToCallCenterOption = verboiceStub.handle(response, connectToCallCenterOption);
        verboiceStub.expect(expectedActions, responseForConnectToCallCenterOption);
    }

    @Test
    public void shouldConnectToCallCenterIfUserOptedForIdWhileAskedToEnterTheMotechIdAfterTheUserHavingEnteredAndInvalidId() {
        String response = verboiceStub.handleIncomingCall();
        String englishLanguageOption = "1";
        response = verboiceStub.handle(response, englishLanguageOption);

        String callCenterPhoneNumber = "0111111111";
        TwiML expectedActions = new TwiML().addAction(new TwiML.Dial(callCenterPhoneNumber, callCenterPhoneNumber));

        String listenToMMMessages = "1";
        response = verboiceStub.handle(response, listenToMMMessages);

        response = verboiceStub.handle(response, "someinvalidmotechid");

        String connectToCallCenterOption = "0";
        String responseForConnectToCallCenterOption = verboiceStub.handle(response, connectToCallCenterOption);
        verboiceStub.expect(expectedActions, responseForConnectToCallCenterOption);

        connectToCallCenterOption = "*";
        responseForConnectToCallCenterOption = verboiceStub.handle(response, connectToCallCenterOption);
        verboiceStub.expect(expectedActions, responseForConnectToCallCenterOption);
    }

    @Test
    public void shouldConnectToCallCenterIfUserDoesNotSelectionAnOptionForReasonForTheCall(){
        String response = verboiceStub.handleIncomingCall();
        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);

        String reasonForCallPrompt = fileName(AudioPrompts.REASON_FOR_CALL_PROMPT);
        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(reasonForCallPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=LzE&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);
    }

    @Test
    public void shouldHandleInboundCallForPatientWithMMRegistrationAndNoMessagesInInbox() {
        String response = verboiceStub.handleIncomingCall();

        String selectLanguagePrompt = fileName(AudioPrompts.LANGUAGE_PROMPT);
        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(selectLanguagePrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=Lw&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);
        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);

        String reasonForCallPrompt = fileName(AudioPrompts.REASON_FOR_CALL_PROMPT);
        expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(reasonForCallPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=LzE&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);

        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);

        String motechIdPrompt = fileName(AudioPrompts.MOTECH_ID_PROMPT);
        expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(motechIdPrompt, "EN"))));
        verboiceStub.expect(expectedActions, response);

        String noMessagesPrompt = fileName(AudioPrompts.NO_MESSAGE_IN_OUTBOX);
        response = verboiceStub.handle(response, patientWithMM.getMotechId());
        expectedActions = new TwiML().addAction(new TwiML.Play(testAppServer.clipPath(noMessagesPrompt, "EN")));
        verboiceStub.expect(expectedActions, response);
    }

    @Test
    public void shouldHandleInboundCallForInvalidMotechId() {
        String response = verboiceStub.handleIncomingCall();

        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);
        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);
        String invalidMotechId = "1";
        response = verboiceStub.handle(response, invalidMotechId);

        String invalidMotechIdPrompt = fileName(AudioPrompts.INVALID_MOTECH_ID_PROMPT);
        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=LzEvMS8x&Digits=timeout")));
        verboiceStub.expect(expectedActions, response);
    }

    @Test
    public void shouldHandleInboundCallIfNotRegisteredToMobileMidwife() {
        Patient patientWithoutMobileMidwifeRegistration = testAppServer.createPatient("patientWithOutMM", "lastName");
        String response = verboiceStub.handleIncomingCall();

        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);
        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);
        response = verboiceStub.handle(response, patientWithoutMobileMidwifeRegistration.getMotechId());

        String invalidMotechIdPrompt = fileName(AudioPrompts.INVALID_MOTECH_ID_PROMPT);
        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=" + new BASE64Encoder().encode(("/1/1/" + patientWithoutMobileMidwifeRegistration.getMotechId()).getBytes()) + "&Digits=timeout")));
        verboiceStub.expect(expectedActions, response);
    }

    @Test
    public void shouldHangTheCallAfter3InvalidMotechId() {
        String response = verboiceStub.handleIncomingCall();

        String englishLanguageChoice = "1";
        response = verboiceStub.handle(response, englishLanguageChoice);
        String listenMessagesChoice = "1";
        response = verboiceStub.handle(response, listenMessagesChoice);
        String invalidMotechId = "1";
        response = verboiceStub.handle(response, invalidMotechId);

        String invalidMotechIdPrompt = fileName(AudioPrompts.INVALID_MOTECH_ID_PROMPT);
        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=" + new BASE64Encoder().encode(("/1/1/" + invalidMotechId).getBytes()) + "&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);

        response = verboiceStub.handle(response, invalidMotechId);

        expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=LzEvMS8xLzE&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);

        response = verboiceStub.handle(response, invalidMotechId);
        expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(invalidMotechIdPrompt, "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=LzEvMS8xLzEvMQ&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);

        response = verboiceStub.handle(response, invalidMotechId);
        expectedActions = new TwiML().addAction(new TwiML.Exit());

        verboiceStub.expect(expectedActions, response);
    }

    @Test
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
                .addAction(new TwiML.Gather());

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
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.GHS), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, thirdMessageResponse);

        // repeat third message

        String repeatThirdMessageOption = "1";
        String thirdMessageRepeatResponse = verboiceStub.handle(thirdMessageResponse, repeatThirdMessageOption);

        expectedActions = new TwiML()
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getClipNames().get(2)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(MobileMidwifeAudioClips.PREGNANCY_WEEK_7.getPromptClipNames().get(2)), "EN")))
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.GHS), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, thirdMessageRepeatResponse);

        // play second message from third message menu

        String secondMessageOption = "2";
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
                .addAction(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.GHS), "EN")))
                .addAction(new TwiML.Gather());

        verboiceStub.expect(expectedActions, response);
    }

    @Test
    public void shouldRepeatLanguageInputMessagesTwiceIfUserDoesNotProvideOneTheFirstTimeAndHangupIfNotOnTheSecondTime() {
        String response = verboiceStub.handleIncomingCall();

        TwiML expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.LANGUAGE_PROMPT), "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=Lw&Digits=timeout")));

        verboiceStub.expect(expectedActions, response);
        response = verboiceStub.handleRedirect(response);

        expectedActions = new TwiML()
                .addAction(new TwiML.Gather().addPrompt(new TwiML.Play(testAppServer.clipPath(fileName(AudioPrompts.LANGUAGE_PROMPT), "EN"))))
                .addAction(new TwiML.Redirect(testAppServer.treePath(INBOUND_DECISION_TREE_NAME, "&trP=L3RpbWVvdXQ&Digits=timeout")));
        verboiceStub.expect(expectedActions, response);
    }

    private String fileName(AudioPrompts audioPrompt) {
        return audioPrompt.value() + ".wav";
    }

    private String fileName(String clipName) {
        return clipName + ".wav";
    }

}
