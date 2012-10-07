package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterCWCFormHandler{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CareService careService;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Autowired
    FacilityService facilityService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(RegisterCWCForm registerCWCForm) {
        try {
            Facility facility = facilityService.getFacilityByMotechId(registerCWCForm.getFacilityId());

            careService.enroll(new CwcVO(registerCWCForm.getStaffId(),
                    facility.getMrsFacilityId(),
                    registerCWCForm.getRegistrationDate(),
                    registerCWCForm.getMotechId(),
                    registerCWCForm.getCWCCareHistories(),
                    registerCWCForm.getBcgDate(),
                    registerCWCForm.getLastVitaminADate(),
                    registerCWCForm.getLastVitaminA(),
                    registerCWCForm.getMeaslesDate(),
                    registerCWCForm.getLastMeasles(),
                    registerCWCForm.getYellowFeverDate(),
                    registerCWCForm.getLastPentaDate(),
                    registerCWCForm.getLastPenta(),
                    registerCWCForm.getLastOPVDate(),
                    registerCWCForm.getLastOPV(),
                    registerCWCForm.getLastIPTiDate(),
                    registerCWCForm.getLastIPTi(),
                    registerCWCForm.getLastRotavirusDate(),
                    registerCWCForm.getLastRotavirus(),
                    registerCWCForm.getLastPneumococcal(),
                    registerCWCForm.getLastPneumococcalDate(),
                    registerCWCForm.getSerialNumber(),
                    registerCWCForm.getAddHistory()));

            MobileMidwifeEnrollment mobileMidwifeEnrollment = registerCWCForm.createMobileMidwifeEnrollment();
            if (mobileMidwifeEnrollment != null) {
                mobileMidwifeService.register(mobileMidwifeEnrollment);
            }
        } catch (Exception e) {
            log.error("Exception occurred while processing CWC Registration form for patientId:"+registerCWCForm.getMotechId(), e);
            throw new XFormHandlerException("Exception occurred while processing CWC Registration form", e);
        }
    }
}
