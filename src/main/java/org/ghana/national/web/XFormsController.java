package org.ghana.national.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.apache.log4j.Logger;
import org.motechproject.proto.MFormsProtocolHandlerImpl;
import org.openxdata.proto.ProtocolHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class XFormsController {
    private Logger log = Logger.getLogger("XformController");

    @RequestMapping(value = "/formdownload", method = RequestMethod.POST)
    public void download(HttpServletRequest request, HttpServletResponse response) {
        log.warn("controller");
        ZOutputStream zOut = null;

        DataInputStream dataIn = null;
        DataOutputStream dataOut = null;

        log.info("incoming request");


        try {
            InputStream in = request.getInputStream();
            OutputStream out = response.getOutputStream();

            dataOut = new DataOutputStream(zOut);

            response.setContentType("application/octet-stream");

            zOut = new ZOutputStream(out, JZlib.Z_BEST_COMPRESSION);

            dataIn = new DataInputStream(in);
            dataOut = new DataOutputStream(zOut);
            log.debug("reading request details");
            String username = dataIn.readUTF();
            String password = dataIn.readUTF();
            String serializer = dataIn.readUTF();
            String locale = dataIn.readUTF();
            String action = request.getParameter("action");

            log.warn(String.format("using %s%s%s%s", username, password, serializer, locale));
            byte actionByte = action == null ? -1 : Byte.parseByte(action);


            log.debug("initializing protocol loader");
            log.debug("loading protocol handler");
            ProtocolHandler protocolHandler = new MFormsProtocolHandlerImpl();
            log.debug("creating submission context");
            protocolHandler.handleRequest(new Context(dataIn, dataOut, actionByte));

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            if (dataOut != null)
                try {
                    dataOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            if (zOut != null)
                try {
                    zOut.finish();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            try {
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }
}


