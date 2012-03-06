package org.motechproject.ghana.national.tools.seed;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MigrateScheduleData {
    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        SeedLoader seedLoader = (SeedLoader) context.getBean("scheduleMigrator");
        seedLoader.load();
        ((ClassPathXmlApplicationContext) context).close();
    }
}
