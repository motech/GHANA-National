package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RegisterANCFormHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CareService careService;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Autowired
    FacilityService facilityService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(RegisterANCForm registerANCForm) {
        try {
            Facility facility = facilityService.getFacilityByMotechId(registerANCForm.getFacilityId());

            Date registrationDate = registerANCForm.getRegistrationDate();
            ANCVO ancvo = new ANCVO(registerANCForm.getStaffId(), facility.getMrsFacilityId(),
                    registerANCForm.getMotechId(), registrationDate, registerANCForm.getRegDateToday(),
                    registerANCForm.getAncRegNumber(), registerANCForm.getEstDeliveryDate(), registerANCForm.getHeight(),
                    registerANCForm.getGravida(), registerANCForm.getParity(), registerANCForm.getAddHistory(), registerANCForm.getDeliveryDateConfirmed(),
                    registerANCForm.getANCCareHistories(), registerANCForm.getLastIPT(), registerANCForm.getLastTT(),
                    registerANCForm.getLastIPTDate(), registerANCForm.getLastTTDate(), registerANCForm.getAddHistory());

            careService.enroll(ancvo);
            MobileMidwifeEnrollment mobileMidwifeEnrollment = registerANCForm.createMobileMidwifeEnrollment();
            if (mobileMidwifeEnrollment != null) {
                mobileMidwifeService.register(mobileMidwifeEnrollment);
            } else {
                mobileMidwifeService.unRegister(registerANCForm.getMotechId());
            }
        } catch (Exception e) {
            log.error("Encountered exception while processing register ANC form", e);
            throw new XFormHandlerException("Encountered exception while processing register ANC form", e);
        }
    }
}
