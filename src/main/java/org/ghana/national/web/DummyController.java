package org.ghana.national.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DummyController {

    @RequestMapping(method = RequestMethod.GET)
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
            outputStream.print(String.format("HEllo %s! This is a Dummy action", this.getClass().getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


