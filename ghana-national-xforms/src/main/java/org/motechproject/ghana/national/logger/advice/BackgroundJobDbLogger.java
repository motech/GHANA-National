package org.motechproject.ghana.national.logger.advice;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.metrics.MetricsAgentBackend;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class BackgroundJobDbLogger implements MetricsAgentBackend {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BackgroundJobDbLogger(@Qualifier("performanceTestlogsDataBase") DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void logEvent(String metric, Map<String, String> parameters) {
        String timeStamp = parameters.get("TimeStamp") != null ? "'" + formatTimeStamp(Long.parseLong(parameters.get("TimeStamp"))) + "'": "null";
        String timeTaken = parameters.get("TimeTaken") != null ? parameters.get("TimeTaken"): "null";
        jdbcTemplate.execute("insert into background_job_logs values('" + metric + "'," + timeStamp + "," + timeTaken + ")");
    }

    private String formatTimeStamp(long timeStamp) {
        return DateUtil.newDateTime(new Date(timeStamp)).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void logEvent(String metric) {
        logEvent(metric, new HashMap<String, String>());
    }

    @Override
    public void logTimedEvent(String metric, final long timeTaken) {
        logEvent(metric, new HashMap<String, String>(){{
            put("TimeTaken", String.valueOf(timeTaken));
            put("TimeStamp", String.valueOf(System.currentTimeMillis()));
        }});
    }
}
