package org.ghana.national.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class XFormsController{
    @RequestMapping(value = "/formdownload",  method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.print("HEllo dude!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
