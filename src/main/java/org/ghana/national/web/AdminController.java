package org.ghana.national.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AdminController {
    private Logger log = Logger.getLogger("AdminController");

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
            outputStream.print("HEllo dude!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


