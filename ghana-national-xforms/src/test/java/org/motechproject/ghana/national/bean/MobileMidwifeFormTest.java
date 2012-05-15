package org.motechproject.ghana.national.bean;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;

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
        return MobileMidwifeBuilder.builderWithSampleData(staffId, facilityId).buildMobileMidwifeForm();
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
