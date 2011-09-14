package org.ghana.national.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/admin/facilities")
public class FacilitiesController {
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(){
        return "admin/facilities/new";
    }
}
