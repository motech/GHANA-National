package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.ANCVisitForm;
import org.motechproject.ghana.national.domain.ANCVisit;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.AllANCVisitsForVisitor;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MotherVisitService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ANCVisitFormHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MotherVisitService motherVisitService;
    @Autowired
    FacilityService facilityService;
    @Autowired
    StaffService staffService;
    @Autowired
    PatientService patientService;
    @Autowired
    AllANCVisitsForVisitor allANCVisitsForVisitor;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(ANCVisitForm ancVisitForm) {
        try {
            ANCVisit ancVisit = createANCVisit(ancVisitForm);
            if (!ancVisit.getVisitor()) {
                motherVisitService.registerANCVisit(ancVisit);
            } else {
                allANCVisitsForVisitor.save(ancVisit);
            }
        } catch (Exception e) {
            log.error("Encountered error while processing ANC visit form for patientId:" + ancVisitForm.getMotechId(), e);
            throw new XFormHandlerException("Encountered error while processing ANC visit form", e);
        }
    }

    private ANCVisit createANCVisit(ANCVisitForm form) {
        Facility facility = facilityService.getFacilityByMotechId(form.getFacilityId());
        MRSUser staff = staffService.getUserByEmailIdOrMotechId(form.getStaffId());
        Patient patient = patientService.getPatientByMotechId(form.getMotechId());

        return new ANCVisit().staff(staff).facility(facility).patient(patient)
                .date(form.getDate()).serialNumber(form.getSerialNumber()).visitNumber(form.getVisitNumber())
                .estDeliveryDate(form.getEstDeliveryDate()).bpDiastolic(form.getBpDiastolic()).bpSystolic(form.getBpSystolic()).pmtct(form.getPmtct())
                .weight(form.getWeight()).ttdose(form.getTtdose()).iptdose(form.getIptdose()).hemoglobin(form.getHemoglobin()).dewormer(form.getDewormer())
                .iptReactive(form.getIptReactive()).itnUse(form.getItnUse()).fhr(form.getFhr()).vdrlReactive(form.getVdrlReactive()).vdrlTreatment(form.getVdrlTreatment())
                .fht(form.getFht()).urineTestProteinPositive(form.getUrineTestProteinPositive()).urineTestGlucosePositive(form.getUrineTestGlucosePositive())
                .preTestCounseled(form.getPreTestCounseled()).hivTestResult(form.getHivTestResult()).postTestCounseled(form.getPostTestCounseled())
                .pmtctTreament(form.getPmtctTreament()).location(form.getLocation()).house(form.getHouse()).community(form.getCommunity())
                .referred(form.getReferred()).maleInvolved(form.getMaleInvolved()).nextANCDate(form.getNextANCDate())
                .comments(form.getComments()).visitor(form.getVisitor()).vdlTreatment(form.getVdltreatment()).hivTreatment(form.getHivtreatment())
                .gestationAge(form.getGestationage()).counseledOnFp(form.getCounseledonfp()).Births(form.getBirths());
    }
}
