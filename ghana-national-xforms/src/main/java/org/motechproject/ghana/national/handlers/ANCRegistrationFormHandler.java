package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ANCRegistrationFormHandler implements FormPublishHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CareService careService;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Autowired
    FacilityService facilityService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.registerANC")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        try {
            RegisterANCForm registerANCForm = (RegisterANCForm) event.getParameters().get(Constants.FORM_BEAN);
            Facility facility = facilityService.getFacilityByMotechId(registerANCForm.getFacilityId());

            ANCVO ancvo = new ANCVO(registerANCForm.getStaffId(), facility.getMrsFacilityId(),
                    registerANCForm.getMotechId(), registerANCForm.getDate(), registerANCForm.getRegDateToday(),
                    registerANCForm.getAncRegNumber(), registerANCForm.getEstDeliveryDate(), registerANCForm.getHeight(),
                    registerANCForm.getGravida(), registerANCForm.getParity(), registerANCForm.getAddHistory(), registerANCForm.getDeliveryDateConfirmed(),
                    registerANCForm.getANCCareHistories(), registerANCForm.getLastIPT(), registerANCForm.getLastTT(),
                    registerANCForm.getLastIPTDate(), registerANCForm.getLastTTDate(), registerANCForm.getAddHistory());

            careService.enroll(ancvo);
            MobileMidwifeEnrollment mobileMidwifeEnrollment = registerANCForm.createMobileMidwifeEnrollment();
            if (mobileMidwifeEnrollment != null) {
                mobileMidwifeService.register(mobileMidwifeEnrollment);
            } else {
                mobileMidwifeService.unregister(registerANCForm.getMotechId());
            }
        } catch (Exception e) {
            log.error("Exception while creating an ANC encounter", e);
        }
    }
}
