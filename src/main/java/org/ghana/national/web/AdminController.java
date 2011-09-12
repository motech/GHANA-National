package org.ghana.national.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private Logger log = Logger.getLogger("AdminController");

    @RequestMapping(method = RequestMethod.GET)
    public String dashboard(){
        return "admin/dashboard";
    }
}


