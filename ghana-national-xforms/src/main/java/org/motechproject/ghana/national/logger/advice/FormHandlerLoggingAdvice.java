package org.motechproject.ghana.national.logger.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.motechproject.metrics.MetricsAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FormHandlerLoggingAdvice {

    private MetricsAgent metricsAgent;

    @Pointcut("execution(* org.motechproject.ghana.national.handler.CareScheduleAlerts.*(..))")
    private void scheduleHandlers(){
    }

    @Pointcut("execution(* org.motechproject.ghana.national.handlers.MobileFormHandler.handleFormEvent(..))")
    private void formAsyncHandlers() {
    }

    @Pointcut("formAsyncHandlers() || scheduleHandlers()")
    private void asyncHandlers(){
    }


    @Autowired
    public FormHandlerLoggingAdvice(@Qualifier("backgroundJobMetricAgent") MetricsAgent metricsAgent) {
        this.metricsAgent = metricsAgent;
    }

    @Around("asyncHandlers()")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = -1;
        try {
            startTime = metricsAgent.startTimer();
            return pjp.proceed();
        } finally {
            String metric = pjp.getTarget().getClass().getName() + "." + pjp.getSignature().getName();
            metricsAgent.stopTimer(metric, startTime);
        }
    }

}
