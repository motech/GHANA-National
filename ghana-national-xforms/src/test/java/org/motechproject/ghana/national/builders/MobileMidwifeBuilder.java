package org.motechproject.ghana.national.builders;

import org.motechproject.ghana.national.bean.*;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

public class MobileMidwifeBuilder {

    private ServiceType serviceType;
    private PhoneOwnership phoneOwnership;
    private String phoneNumber;
    private String format;
    private DayOfWeek dayOfWeek;
    private Time timeOfDay;
    private Language language;
    private LearnedFrom learnedFrom;
    private ReasonToJoin reasonToJoin;
    private String messageStartWeek;

    private String staffId;
    private String facilityId;
    private String patientId;
    private Boolean consent;

    private Boolean enroll;

    public MobileMidwifeBuilder serviceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public MobileMidwifeBuilder phoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
        return this;
    }

    public MobileMidwifeBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public MobileMidwifeBuilder format(String medium) {
        this.format = medium;
        return this;
    }

    public MobileMidwifeBuilder timeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
        return this;
    }

    public MobileMidwifeBuilder messageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
        return this;
    }

    public MobileMidwifeBuilder dayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public MobileMidwifeBuilder learnedFrom(LearnedFrom learnedFrom) {
        this.learnedFrom = learnedFrom;
        return this;
    }

    public MobileMidwifeBuilder reasonToJoin(ReasonToJoin reasonToJoin) {
        this.reasonToJoin = reasonToJoin;
        return this;
    }

    public MobileMidwifeBuilder language(Language language) {
        this.language = language;
        return this;
    }


    public MobileMidwifeBuilder staffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public MobileMidwifeBuilder facilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public MobileMidwifeBuilder patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public MobileMidwifeBuilder consent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public MobileMidwifeBuilder enroll(Boolean enroll) {
        this.enroll = enroll;
        return this;
    }

    public MobileMidwifeForm buildMobileMidwifeForm() {
        MobileMidwifeForm mobileMidwifeForm = new MobileMidwifeForm();
        mobileMidwifeForm.setStaffId(staffId);
        mobileMidwifeForm.setFacilityId(facilityId);
        mobileMidwifeForm.setPatientId(patientId);
        mobileMidwifeForm.setConsent(consent);
        fillAbstractFormFields(mobileMidwifeForm);
        return mobileMidwifeForm;
    }

    private void fillAbstractFormFields(AbstractMobileMidWifeForm mobileMidwifeForm) {
        mobileMidwifeForm.setDayOfWeek(dayOfWeek);
        mobileMidwifeForm.setLanguage(language);
        mobileMidwifeForm.setLearnedFrom(learnedFrom);
        mobileMidwifeForm.setFormat(format);
        mobileMidwifeForm.setMessageStartWeek(messageStartWeek);
        mobileMidwifeForm.setPhoneNumber(phoneNumber);
        mobileMidwifeForm.setPhoneOwnership(phoneOwnership);
        mobileMidwifeForm.setReasonToJoin(reasonToJoin);
        mobileMidwifeForm.setServiceType(serviceType);
        mobileMidwifeForm.setTimeOfDay(timeOfDay);
    }

    public RegisterANCForm buildRegisterANCForm(RegisterANCForm registerANCForm) {
        buildIncludeMobileForm(registerANCForm);
        if(staffId != null) registerANCForm.setStaffId(staffId);
        if(facilityId != null) registerANCForm.setFacilityId(facilityId);
        if(patientId != null) registerANCForm.setMotechId(patientId);
        return registerANCForm;
    }

    public RegisterCWCForm buildRegisterCWCForm(RegisterCWCForm registerCWCForm) {
        buildIncludeMobileForm(registerCWCForm);
        if(staffId != null) registerCWCForm.setStaffId(staffId);
        if(facilityId != null) registerCWCForm.setFacilityId(facilityId);
        if(patientId != null) registerCWCForm.setMotechId(patientId);
        return registerCWCForm;
    }

    private void buildIncludeMobileForm(MobileMidWifeIncludeForm includeForm) {
        fillAbstractFormFields(includeForm);
        includeForm.setConsent(consent);
        includeForm.setEnroll(enroll);
    }
}
