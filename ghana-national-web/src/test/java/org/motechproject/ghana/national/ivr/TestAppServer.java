package org.motechproject.ghana.national.ivr;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.MotechException;
import org.springframework.web.servlet.DispatcherServlet;

public class TestAppServer {
    public static final int PORT = 8080;
    public static final String CONTEXT_PATH = "/ghana-national-web";

    private Server applicationServer;

    public void startApplication(){

        applicationServer = new Server(PORT);
        Context context = new Context(applicationServer, CONTEXT_PATH);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:applicationContext.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        applicationServer.setHandler(context);

        try {
            applicationServer.start();
        } catch (Exception e) {
            throw new MotechException("Unable to start application server", e);
        }
    }

    private String contextURL(){
        return "http://localhost:" + PORT + CONTEXT_PATH;
    }

    public String path(String path) {
        return contextURL() + path;
    }

    public String clipPath(String prompt, String language) {
        return path("/resource/stream/" + language + "/" + prompt);
    }
}
