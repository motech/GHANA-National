package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class RegisterANCFormHandler implements FormPublishHandler {
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

            Date registrationDate = registerANCForm.getDate() == null ? DateUtil.today().toDate() : registerANCForm.getDate();
            ANCVO ancvo = new ANCVO(registerANCForm.getStaffId(), facility.getMrsFacilityId(),
                    registerANCForm.getMotechId(), registrationDate, registerANCForm.getRegDateToday(),
                    registerANCForm.getAncRegNumber(), registerANCForm.getEstDeliveryDate(), registerANCForm.getHeight(),
                    registerANCForm.getGravida(), registerANCForm.getParity(), registerANCForm.getAddHistory(), registerANCForm.getDeliveryDateConfirmed(),
                    registerANCForm.getANCCareHistories(), registerANCForm.getLastIPT(), registerANCForm.getLastTT(),
                    registerANCForm.getLastIPTDate(), registerANCForm.getLastTTDate(), registerANCForm.getAddHistory());

            careService.enroll(ancvo);
            MobileMidwifeEnrollment mobileMidwifeEnrollment = registerANCForm.createMobileMidwifeEnrollment();
            if (mobileMidwifeEnrollment != null) {
                mobileMidwifeService.register(mobileMidwifeEnrollment.setEnrollmentDateTime(newDateTime(registrationDate)));
            } else {
                mobileMidwifeService.unRegister(registerANCForm.getMotechId());
            }
        } catch (Exception e) {
            log.error("Exception while creating an ANC encounter", e);
            throw new XFormHandlerException(event.getSubject(), e);
        }
    }
}
