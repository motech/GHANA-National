package org.ghana.national.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/admin")
public class AdminController extends DashboardController{
    private Logger log = Logger.getLogger("AdminController");

}


