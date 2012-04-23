package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.util.DateUtil.*;

public class OutPatientVisitTest {

    @Test
    public void shouldTestEquality() {

        assertThat(outpatientVisit(), is(outpatientVisit()));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setActTreated(false))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setComments("comments2"))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setDateOfBirth(null))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setDiagnosis(2))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setGender("f"))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setGender("fac B"))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setInsured(false))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setNewCase(false))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setNewPatient(false))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setNhis(null))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setNhisExpires(null))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setRdtGiven(false))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setRdtPositive(true))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setReferred(false))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setRegistrantType(null))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setSecondDiagnosis(1))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setStaffId(null))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setSerialNumber(null))));
        assertThat(outpatientVisit(), not(is(outpatientVisit().setVisitDate(null))));
    }

    private OutPatientVisit outpatientVisit() {
           return new OutPatientVisit().setActTreated(true).setComments("comments").setDateOfBirth(newDateTime(2012, 2, 2, 1, 1, 1).toDate())
                   .setDiagnosis(1).setGender("M").setFacilityId("fac A")
                   .setInsured(true).setNewCase(true).setNewPatient(true).setNhis("Insurance").setNhisExpires(today().plusYears(3).toDate()).setRdtGiven(true).setRdtPositive(false).setReferred(true)
                   .setRegistrantType(PatientType.OTHER).setSecondDiagnosis(2).setStaffId("staffid1").setSerialNumber("1234").setVisitDate(newDate(2012, 3, 3).toDate());
       }

}
