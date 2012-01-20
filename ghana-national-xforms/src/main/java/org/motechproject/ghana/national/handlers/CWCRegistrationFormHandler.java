package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
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
    CareService careService;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Autowired
    FacilityService facilityService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.registerCWC")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        RegisterCWCForm registerCWCForm = (RegisterCWCForm) event.getParameters().get(FORM_BEAN);

        Facility facility = facilityService.getFacilityByMotechId(registerCWCForm.getFacilityId());

        careService.enroll(new CwcVO(registerCWCForm.getStaffId(),
                facility.getMrsFacilityId(),
                registerCWCForm.getRegistrationDate(),
                registerCWCForm.getMotechId(),
                registerCWCForm.getCWCCareHistories(), registerCWCForm.getBcgDate(),
                registerCWCForm.getLastVitaminADate(),
                registerCWCForm.getMeaslesDate(),
                registerCWCForm.getYellowFeverDate(),
                registerCWCForm.getLastPentaDate(),
                registerCWCForm.getLastPenta(),
                registerCWCForm.getLastOPVDate(),
                registerCWCForm.getLastOPV(),
                registerCWCForm.getLastIPTiDate(),
                registerCWCForm.getLastIPTi(),
                registerCWCForm.getSerialNumber()));

        MobileMidwifeEnrollment mobileMidwifeEnrollment = registerCWCForm.createMobileMidwifeEnrollment();
        if(mobileMidwifeEnrollment != null){
            mobileMidwifeService.createOrUpdateEnrollment(mobileMidwifeEnrollment);
        }
    }
}
