package org.motechproject.openmrs.omod.web.servlet;

import org.motechproject.openmrs.omod.service.OmodIdentifierService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class OmodIdentifierServlet extends HttpServlet {
    private static final String ID_GENERATOR = "generator";
    private static final String ID_TYPE = "type";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String generatedId = getOmodIdentifierService().getIdFor(
                request.getParameter(ID_GENERATOR),
                request.getParameter(ID_TYPE),
                request.getParameter(USER),
                request.getParameter(PASSWORD)
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(generatedId);
        writer.close();
    }

    protected OmodIdentifierService getOmodIdentifierService() {
        return new OmodIdentifierService();
    }
}
