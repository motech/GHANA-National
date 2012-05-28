package org.motechproject.ghana.national.logger.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Date;

@Aspect
@Component
public class FormHandlerLoggingAdvice {
    @Pointcut("execution(* org.motechproject.ghana.national.handler.CareScheduleAlerts.*(..))")
    private void scheduleHandlers(){
    }

//    @Pointcut("execution(* org.motechproject.ghana.national.handlers.*.handleFormEvent(..))")
    @Pointcut("execution(* org.motechproject.ghana.national.handlers.MobileFormHandler.handleFormEvent(..))")
    private void formAsyncHandlers() {
    }

    @Pointcut("formAsyncHandlers() || scheduleHandlers()")
    private void asyncHandlers(){

    }

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public FormHandlerLoggingAdvice(@Qualifier("performanceTestlogsDataBase") DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Around("asyncHandlers()")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = -1;
        try {
            startTime = System.currentTimeMillis();
            return pjp.proceed();
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            final String className = pjp.getTarget().getClass().getName();
            jdbcTemplate.execute("insert into background_job_logs values('" + className + "','" +
                    pjp.getSignature().getName() + "','" + DateUtil.newDateTime(new Date(startTime)).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")) +
                    "'," + timeTaken + ")");
        }
    }

}
