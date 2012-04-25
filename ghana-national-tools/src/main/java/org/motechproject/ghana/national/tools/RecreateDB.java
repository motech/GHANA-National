package org.motechproject.ghana.national.tools;

import org.motechproject.ghana.national.tools.seed.util.CouchDB;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RecreateDB {
    static org.slf4j.Logger log = LoggerFactory.getLogger(RecreateDB.class);
    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";

    public static void main(String[] args) {
        log.info("Recreate DB: START");
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        CouchDB couchDB = context.getBean(CouchDB.class);
        couchDB.recreate();
        ((ClassPathXmlApplicationContext) context).close();
        log.info("Recreate DB: END");
    }
}