package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CareHistoryFormHandler{

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CareService careService;

    @Autowired
    private FacilityService facilityService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(CareHistoryForm careHistoryForm) {
        try {
            String facilityId = facilityService.getFacilityByMotechId(careHistoryForm.getFacilityId()).getMrsFacilityId();
            careService.addCareHistory(careHistoryForm.careHistoryVO(facilityId));
        } catch (Exception e) {
            log.error("Encountered error while processing care history form", e);
            throw new XFormHandlerException("Encountered error while processing care history form", e);
        }
    }
}
