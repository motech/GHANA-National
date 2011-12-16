package org.motechproject.functional.util;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: karthikm
 * Date: 12/14/11
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Utilities {
    SimpleSmtpServer smtpServer;

    public void mySleep() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void mySleep(int x) {
        try {
            Thread.sleep(x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startSMTP() {
        smtpServer = SimpleSmtpServer.start();
    }

    public String checkMessages() {
        Iterator emailIter = smtpServer.getReceivedEmail();

        SmtpMessage smtpMessage = (SmtpMessage) emailIter.next();

        String msgBody =smtpMessage.getBody();
        System.out.println("Message Body" + msgBody);

        System.out.print("-----------------------------------------");

        if(msgBody.contains("Password :"))
        {
            System.out.println("Password is present in the mail body");
        }

        smtpServer.stop();
        return "password";
    }
}

