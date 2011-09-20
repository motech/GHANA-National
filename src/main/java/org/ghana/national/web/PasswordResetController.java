package org.ghana.national.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/security/password/reset")
public class PasswordResetController {
    private Logger log = Logger.getLogger("PasswordResetController");

    @RequestMapping(method = RequestMethod.GET)
    public String passwordResetForm(){
        return "password/reset";
    }
}


