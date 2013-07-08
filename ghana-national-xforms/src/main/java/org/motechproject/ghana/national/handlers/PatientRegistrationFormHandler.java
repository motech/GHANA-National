package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.ghana.national.domain.SmsTemplateKeys.REGISTER_SUCCESS_SMS_KEY;


@Component
public class PatientRegistrationFormHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PatientService patientService;

    @Autowired
    private CareService careService;

    @Autowired
    private MobileMidwifeService mobileMidwifeService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private SMSGateway smsGateway;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(RegisterClientForm registerClientForm) {
        try {
            MRSPerson mrsPerson = new MRSPerson().
                    firstName(registerClientForm.getFirstName()).
                    middleName(registerClientForm.getMiddleName()).
                    lastName(registerClientForm.getLastName()).
                    dateOfBirth(registerClientForm.getDateOfBirth()).
                    birthDateEstimated(registerClientForm.getEstimatedBirthDate()).
                    gender(registerClientForm.getSex()).
                    address(registerClientForm.getAddress()).
                    attributes(getPatientAttributes(registerClientForm));

            String facilityId = facilityService.getFacilityByMotechId(registerClientForm.getFacilityId()).mrsFacilityId();
            MRSPatient mrsPatient = new MRSPatient(registerClientForm.getMotechId(), mrsPerson, new MRSFacility(facilityId));

            Patient patient = new Patient(mrsPatient, registerClientForm.getMotherMotechId());
            final Patient savedPatient = patientService.registerPatient(patient, registerClientForm.getStaffId(), registerClientForm.getDate());

            registerForMobileMidwifeProgram(registerClientForm, savedPatient.getMotechId());
            registerForCWC(registerClientForm, facilityId, savedPatient.getMotechId());
            registerForANC(registerClientForm, facilityId, savedPatient.getMotechId());


            //added for Child's motech id.
            Patient patient1 = new Patient(mrsPatient, registerClientForm.getChildMotechId());
            final Patient savedPatient1 = patientService.registerPatient(patient1, registerClientForm.getStaffId(), registerClientForm.getDate());

            if (registerClientForm.getChildMotechId() != null){
                registerForMobileMidwifeProgram(registerClientForm, savedPatient1.getMotechId());
                registerForCWC(registerClientForm, facilityId, savedPatient1.getMotechId());
                registerForANC(registerClientForm, facilityId, savedPatient1.getMotechId());
            }


            if (registerClientForm.getSender() != null) {
                smsGateway.dispatchSMS(REGISTER_SUCCESS_SMS_KEY,
                        new SMSTemplate().fillPatientDetails(savedPatient).getRuntimeVariables(), registerClientForm.getSender());
            }
        } catch (Exception e) {
            log.error("Encountered exception while processing patient reg form", e);
            throw new XFormHandlerException("Encountered exception while processing patient reg form", e);
        }
    }

    private void registerForMobileMidwifeProgram(RegisterClientForm registerClientForm, String patientMotechId) {
        if (registerClientForm.isEnrolledForMobileMidwifeProgram()) {
            MobileMidwifeEnrollment mobileMidwifeEnrollment = registerClientForm.createMobileMidwifeEnrollment(patientMotechId);
            mobileMidwifeEnrollment.setFacilityId(registerClientForm.getFacilityId());
            mobileMidwifeService.register(mobileMidwifeEnrollment);
        }
    }

    private void registerForANC(RegisterClientForm registerClientForm, String facilityId, String patientMotechId) throws ObservationNotFoundException {
        if (PatientType.PREGNANT_MOTHER.equals(registerClientForm.getRegistrantType())) {
            ANCVO ancVO = new ANCVO(registerClientForm.getStaffId(), facilityId, patientMotechId, registerClientForm.getDate()
                    , RegistrationToday.TODAY, registerClientForm.getAncRegNumber(), registerClientForm.getExpDeliveryDate(), registerClientForm.getHeight(), registerClientForm.getGravida(),
                    registerClientForm.getParity(), registerClientForm.getAddHistory(), registerClientForm.getDeliveryDateConfirmed(), registerClientForm.getAncCareHistories(), registerClientForm.getLastIPT(), registerClientForm.getLastTT(),
                    registerClientForm.getLastHbLevels(),registerClientForm.getLastMotherVitaminA(),registerClientForm.getLastIronOrFolate(),registerClientForm.getLastSyphilis(), registerClientForm.getLastIPTDate(), registerClientForm.getLastTTDate(),
                    registerClientForm.getLastHbDate(),registerClientForm.getLastMotherVitaminADate(),registerClientForm.getLastIronOrFolateDate(),
                    registerClientForm.getLastSyphilisDate(), registerClientForm.getAddHistory());

            careService.enroll(ancVO);
        }
        if (PatientType.MOTHER_OF_INFANT.equals(registerClientForm.getRegistrantType())) {
            ANCVO ancVO = new ANCVO(registerClientForm.getStaffId(), facilityId, patientMotechId, registerClientForm.getDate()
                    , RegistrationToday.TODAY, registerClientForm.getAncRegNumber(), registerClientForm.getExpDeliveryDate(), registerClientForm.getHeight(), registerClientForm.getGravida(),
                    registerClientForm.getParity(), registerClientForm.getAddHistory(), registerClientForm.getDeliveryDateConfirmed(), registerClientForm.getAncCareHistories(), registerClientForm.getLastmIPT(), registerClientForm.getLastmTT(),
                    registerClientForm.getLastmHbLevels(),registerClientForm.getLastmMotherVitaminA(),registerClientForm.getLastmIronOrFolate(),registerClientForm.getLastmSyphilis(),registerClientForm.getLastmIPTDate(), registerClientForm.getLastmTTDate(),
                    registerClientForm.getLastmHbDate(),registerClientForm.getLastmMotherVitaminADate(),registerClientForm.getLastmIronOrFolateDate(),
                    registerClientForm.getLastmSyphilisDate(), registerClientForm.getAddHistory());

            careService.enroll(ancVO);
        }

    }

    private void registerForCWC(RegisterClientForm registerClientForm, String facilityId, String patientMotechId) {
        if (registerClientForm.getRegistrantType().equals(PatientType.CHILD_UNDER_FIVE)) {
            CwcVO cwcVO = new CwcVO(registerClientForm.getStaffId(), facilityId, registerClientForm.getDate(),
                    patientMotechId, registerClientForm.getCWCCareHistories(), registerClientForm.getBcgDate(), registerClientForm.getLastVitaminADate(), registerClientForm.getLastVitaminA(), registerClientForm.getMeaslesDate(),
                    registerClientForm.getLastMeasles(), registerClientForm.getYellowFeverDate(), registerClientForm.getLastPentaDate(), registerClientForm.getLastPenta(), registerClientForm.getLastOPVDate(),
                    registerClientForm.getLastOPV(), registerClientForm.getLastIPTiDate(), registerClientForm.getLastIPTi(), registerClientForm.getLastRotavirusDate(), registerClientForm.getLastRotavirus(),
                    registerClientForm.getLastPneumococcal(), registerClientForm.getLastPneumococcalDate(), registerClientForm.getLastMalaria(), registerClientForm.getLastMalariaDate(),
                    registerClientForm.getLastPnuemonia(), registerClientForm.getLastPnuemoniaDate(), registerClientForm.getLastmDiarrhea(), registerClientForm.getLastmDiarrheaDate(), registerClientForm.getCwcRegNumber(), registerClientForm.getAddHistory());

            careService.enroll(cwcVO);
        }
    }

    private List<Attribute> getPatientAttributes(RegisterClientForm registerClientForm) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), registerClientForm.getPhoneNumber()));

        Date nhisExpirationDate = registerClientForm.getNhisExpires();
        if (nhisExpirationDate != null) {
            attributes.add(new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute(), new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD).format(nhisExpirationDate)));
        }
        attributes.add(new Attribute(PatientAttributes.NHIS_NUMBER.getAttribute(), registerClientForm.getNhis()));
        attributes.add(new Attribute(PatientAttributes.INSURED.getAttribute(), Utility.safeToString(registerClientForm.getInsured())));
        return attributes;
    }
}
