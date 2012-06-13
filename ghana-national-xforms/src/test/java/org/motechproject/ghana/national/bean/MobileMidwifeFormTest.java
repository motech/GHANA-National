package org.motechproject.ghana.national.bean;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;

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

    public static void assertFormWithEnrollment(MobileMidwifeForm expectedForm, MobileMidwifeEnrollment actual) {
        assertThat(actual.getStaffId(), is(expectedForm.getStaffId()));
        assertThat(actual.getFacilityId(), is(expectedForm.getFacilityId()));
        assertThat(actual.getPatientId(), is(expectedForm.getMotechId()));
        assertThat(actual.getServiceType(), is(ServiceType.getServiceType(expectedForm.getServiceType(),expectedForm.getMediumStripingOwnership())));
        assertThat(actual.getReasonToJoin(), is(expectedForm.getReasonToJoin()));
        assertThat(actual.getMedium(), is(expectedForm.getMediumStripingOwnership()));
        assertThat(actual.getDayOfWeek(), is(expectedForm.getDayOfWeek()));
        assertThat(actual.getTimeOfDay(), is(expectedForm.getTimeOfDay()));
        assertThat(actual.getLanguage(), is(expectedForm.getLanguage()));
        assertThat(actual.getLearnedFrom(), is(expectedForm.getLearnedFrom()));
        assertThat(actual.getPhoneNumber(), is(expectedForm.getMmRegPhone()));
        assertThat(actual.getPhoneOwnership(), is(expectedForm.getPhoneOwnership()));
        assertThat(actual.getConsent(), is(expectedForm.getConsent()));
        assertThat(actual.getEnrollmentDateTime().toLocalDate(), is(new LocalDate()));
    }

}
