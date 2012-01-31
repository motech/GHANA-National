package org.motechproject.ghana.national.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.motechproject.ghana.national.domain.SMSAudit;
import org.motechproject.ghana.national.repository.AllSMS;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class TextMessageServiceAspect {

    @Autowired
    private AllSMS allSMS;

    @Pointcut("@annotation(org.motechproject.ghana.national.domain.Auditable)")
    public void logSmsAudits() {
    }

    @Around("logSmsAudits()")
    public String auditMessage(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String recipient = (String) proceedingJoinPoint.getArgs()[0];
        String message = (String) proceedingJoinPoint.proceed();
        allSMS.add(new SMSAudit(recipient, message, DateUtil.now()));
        return message;
    }
}
