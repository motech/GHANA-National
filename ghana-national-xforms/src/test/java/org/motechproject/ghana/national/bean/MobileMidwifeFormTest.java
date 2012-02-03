package org.motechproject.ghana.national.bean;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MobileMidwifeFormTest {
    @Test
    public void shouldCreateValueObjectFromFormInstance(){
        String staffId = "staff id";
        String facilityId = "facility id";
        MobileMidwifeForm mobileMidwifeForm = setupFormData(staffId, facilityId);
        assertFormWithEnrollment(mobileMidwifeForm, mobileMidwifeForm.createMobileMidwifeEnrollment());
    }

    public static MobileMidwifeForm setupFormData(String staffId, String facilityId){
        return builderWithSampleData(staffId, facilityId).buildMobileMidwifeForm();
    }

    private static MobileMidwifeBuilder builderWithSampleData(String staffId, String facilityId) {
        return new MobileMidwifeBuilder().staffId(staffId).facilityId(facilityId).patientId("patientId")
                .consent(true).dayOfWeek(DayOfWeek.Monday).language(Language.EN).learnedFrom(LearnedFrom.FRIEND).format("PERS_VOICE")
                .timeOfDay(new Time(10, 0)).messageStartWeek("10").phoneNumber("9500012343")
                .phoneOwnership(PhoneOwnership.PERSONAL).reasonToJoin(ReasonToJoin.FAMILY_FRIEND_DELIVERED)
                .serviceType(ServiceType.CHILD_CARE);
    }

    public static void assertFormWithEnrollment(MobileMidwifeForm exptectedForm, MobileMidwifeEnrollment actual) {
        assertThat(actual.getStaffId(), is(exptectedForm.getStaffId()));
        assertThat(actual.getFacilityId(), is(exptectedForm.getFacilityId()));
        assertThat(actual.getPatientId(), is(exptectedForm.getPatientId()));
        assertThat(actual.getServiceType(), is(exptectedForm.getServiceType()));
        assertThat(actual.getReasonToJoin(), is(exptectedForm.getReasonToJoin()));
        assertThat(actual.getMedium(), is(exptectedForm.getMediumStripingOwnership()));
        assertThat(actual.getDayOfWeek(), is(exptectedForm.getDayOfWeek()));
        assertThat(actual.getTimeOfDay(), is(exptectedForm.getTimeOfDay()));
        assertThat(actual.getLanguage(), is(exptectedForm.getLanguage()));
        assertThat(actual.getLearnedFrom(), is(exptectedForm.getLearnedFrom()));
        assertThat(actual.getPhoneNumber(), is(exptectedForm.getMmRegPhone()));
        assertThat(actual.getPhoneOwnership(), is(exptectedForm.getPhoneOwnership()));
        assertThat(actual.getConsent(), is(exptectedForm.getConsent()));
        assertThat(actual.getEnrollmentDateTime().toLocalDate(), is(new LocalDate()));
    }

}
