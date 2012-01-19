package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.CWCService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.CwcVO;
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
public class CWCRegistrationFormHandler implements FormPublishHandler {
    public static final String FORM_BEAN = "formBean";
    static final String CWCREGVISIT = "CWCREGVISIT";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CWCService cwcService;

    @Autowired
    FacilityService facilityService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.registerCWC")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        final RegisterCWCForm registerCWCForm = (RegisterCWCForm) event.getParameters().get(FORM_BEAN);

        final Facility facility = facilityService.getFacilityByMotechId(registerCWCForm.getFacilityId());

        cwcService.enrollWithMobileMidwife(new CwcVO(registerCWCForm.getStaffId(),
                facility.getMrsFacilityId(),
                registerCWCForm.getRegistrationDate(),
                registerCWCForm.getMotechId(),
                null,
                registerCWCForm.getBcgDate(),
                registerCWCForm.getLastVitaminADate(),
                registerCWCForm.getMeaslesDate(),
                registerCWCForm.getYellowFeverDate(),
                registerCWCForm.getLastPentaDate(),
                registerCWCForm.getLastPenta(),
                registerCWCForm.getLastOPVDate(),
                registerCWCForm.getLastOPV(),
                registerCWCForm.getLastIPTiDate(),
                registerCWCForm.getLastIPTi(),
                registerCWCForm.getSerialNumber()),
                registerCWCForm.createMobileMidwifeEnrollment());
    }
}
