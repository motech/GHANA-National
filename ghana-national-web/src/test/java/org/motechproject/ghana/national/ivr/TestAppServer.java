package org.motechproject.ghana.national.ivr;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.service.ExecuteAsOpenMRSAdmin;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.tools.TestDataGenerator;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.util.DateUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class TestAppServer {
    public static final int PORT = 8080;
    public static final String GN_CONTEXT_PATH = "/ghana-national-web";
    private static final String OPENMRS_CONTEXT_PATH = "/openmrs";

    private Server applicationServer;
    private DispatcherServlet dispatcherServlet;

    public void startApplication() {

        applicationServer = new Server(PORT);
        Context context = new Context(applicationServer, GN_CONTEXT_PATH);
        dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:applicationContext_ivr_test.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        applicationServer.addHandler(context);
        try {
            applicationServer.start();
        } catch (Exception e) {
            throw new MotechException("Unable to start application server", e);
        }
    }

    private String contextURL() {
        return "http://localhost:" + PORT + GN_CONTEXT_PATH;
    }

    public String path(String path) {
        return contextURL() + path;
    }

    public String clipPath(String prompt, String language) {
        return path("/resource/stream/" + language + "/" + prompt);
    }

    public String createStaff(final String firstName) {
        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) webApplicationContext.getBean("executeAsOpenMRSAdmin");
        final TestDataGenerator testDataGenerator = (TestDataGenerator) webApplicationContext.getBean("testDataGenerator");
        return (String) executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                try {
                    return testDataGenerator.createStaff(firstName, "0123456789");
                } catch (UserAlreadyExistsException e) {
                    throw new MotechException("Encountered exception while creating staff", e);
                }
            }
        });
    }

    public String createFacility() {
        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) webApplicationContext.getBean("executeAsOpenMRSAdmin");
        final TestDataGenerator testDataGenerator = (TestDataGenerator) webApplicationContext.getBean("testDataGenerator");
        return (String) executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                return testDataGenerator.getAFacility().get(0).mrsFacility().getId();
            }
        });
    }

    public Patient createPatient(final String firstName, final String lastName) {
        final String staffId = createStaff(firstName);
        final String facilityId = createFacility();

        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        final TestDataGenerator testDataGenerator = (TestDataGenerator) webApplicationContext.getBean("testDataGenerator");

        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) webApplicationContext.getBean("executeAsOpenMRSAdmin");
        return (Patient) executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                try {
                    return testDataGenerator.createPatient(firstName, lastName, DateUtil.now().minusYears(25).toDate(), facilityId, staffId, "0987654321");
                } catch (Exception e) {
                    new MotechException("Patient creation failed ", e);
                }
                return null;
            }
        });
    }

    public void registerForMobileMidwife(final Patient patient) {
        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        final MobileMidwifeService mobileMidwifeService = (MobileMidwifeService) webApplicationContext.getBean("mobileMidwifeService");
        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) webApplicationContext.getBean("executeAsOpenMRSAdmin");
        executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(DateTime.now());
                enrollment.setLanguage(Language.EN);
                enrollment.setMedium(Medium.VOICE);
                enrollment.setPhoneNumber(patient.getPhoneNumber());
                enrollment.setActive(Boolean.TRUE);
                enrollment.setConsent(Boolean.TRUE);
                enrollment.setDayOfWeek(DayOfWeek.Monday);
                enrollment.setEnrollmentDateTime(DateTime.now());
                enrollment.setFacilityId((String) patient.facilityMetaData().get("facilityId"));
                enrollment.setLearnedFrom(LearnedFrom.FAMILY_MEMBER);
                enrollment.setPatientId(patient.getMotechId());
                enrollment.setMessageStartWeek("6");
                enrollment.setReasonToJoin(ReasonToJoin.CURRENTLY_PREGNANT);
                enrollment.setServiceType(ServiceType.PREGNANCY);
                enrollment.setTimeOfDay(new Time(10, 30));

                mobileMidwifeService.register(enrollment);
                return null;
            }
        });

    }

    public void addCareMessageToOutbox(String motechId, String clipName, DateTime windowStart) {
        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        AllPatientsOutbox allPatientsOutbox = (AllPatientsOutbox) webApplicationContext.getBean("allPatientsOutbox");
        allPatientsOutbox.addCareMessage(motechId, clipName, Period.weeks(1), AlertWindow.DUE, windowStart);
    }

    public void shutdownApplication() {
        try {
            applicationServer.stop();
        } catch (Exception e) {
            throw new MotechException("Unable to stop application", e);
        }
    }

    public void addAppointmentMessageToOutbox(String motechId, String clipName) {
        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        AllPatientsOutbox allPatientsOutbox = (AllPatientsOutbox) webApplicationContext.getBean("allPatientsOutbox");
        allPatientsOutbox.addAppointmentMessage(motechId, clipName, Period.weeks(1));
    }

    public void addMobileMidwifeMessageToOutbox(String motechId, MobileMidwifeAudioClips mobileMidwifeAudioClips) {
        final WebApplicationContext webApplicationContext = dispatcherServlet.getWebApplicationContext();
        AllPatientsOutbox allPatientsOutbox = (AllPatientsOutbox) webApplicationContext.getBean("allPatientsOutbox");
        allPatientsOutbox.addMobileMidwifeMessage(motechId, mobileMidwifeAudioClips, Period.weeks(1));
    }
}
