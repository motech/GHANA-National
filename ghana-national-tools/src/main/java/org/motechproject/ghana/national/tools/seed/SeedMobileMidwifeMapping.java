package org.motechproject.ghana.national.tools.seed;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: motech
 * Date: 8/6/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeedMobileMidwifeMapping {

    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        SeedLoader seedLoader = (SeedLoader) context.getBean("mmScheduleMigrator");
        seedLoader.load();
    }

}
