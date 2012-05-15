package org.motechproject.ghana.national.logger.configuration;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.motechproject.MotechException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Configuration
public class LoggerDatabaseConfiguration {

    // create table background_job_logs(class_name varchar(250), method_name varchar(250), st_time datetime, duration mediumint unsigned);
    @Bean(name = "performanceTestlogsDataBase")
    public DataSource dataSource(){
        try {
            final ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
            comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/motech_logs?autoReconnect=true");
            comboPooledDataSource.setUser("root");
            comboPooledDataSource.setPassword("password");
            comboPooledDataSource.setAcquireIncrement(5);
            comboPooledDataSource.setMinPoolSize(30);
            comboPooledDataSource.setMaxPoolSize(50);
            comboPooledDataSource.setMaxIdleTime(3600);
            return comboPooledDataSource;
        } catch (PropertyVetoException e) {
            throw new MotechException("Encountered error while creating datasource for logging", e);
        }
    }
}
