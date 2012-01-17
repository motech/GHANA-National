package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.ANCService;
import org.motechproject.ghana.national.service.FacilityService;
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
    public static final String FORM_BEAN = "formBean";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ANCService ancService;
    @Autowired
    private FacilityService facilityService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.registerANC")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        RegisterANCForm registerANCForm = (RegisterANCForm) event.getParameters().get(FORM_BEAN);
         final Facility facility = facilityService.getFacilityByMotechId(registerANCForm.getFacilityId());

        ANCVO ancvo = new ANCVO(registerANCForm.getStaffId(), facility.getMrsFacilityId(),
                registerANCForm.getMotechId(), registerANCForm.getDate(), registerANCForm.getRegDateToday(),
                registerANCForm.getAncRegNumber(), registerANCForm.getEstDeliveryDate(), registerANCForm.getHeight(),
                registerANCForm.getGravida(), registerANCForm.getParity(), registerANCForm.getAddHistory(), registerANCForm.getDeliveryDateConfirmed(),
                null, registerANCForm.getLastIPT(), registerANCForm.getLastTT(),
                registerANCForm.getLastIPTDate(), registerANCForm.getLastTTDate());

        ancService.enroll(ancvo);
    }
}
