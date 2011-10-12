package org.ghana.national.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class DashboardController {

    @RequestMapping(method = RequestMethod.GET)
    public String dashboard() {
        return "common/dashboard";
    }
}


