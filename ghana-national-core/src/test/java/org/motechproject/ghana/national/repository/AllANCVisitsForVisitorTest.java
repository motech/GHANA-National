package org.motechproject.ghana.national.repository;

import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.ANCVisit;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static junit.framework.Assert.assertTrue;

public class AllANCVisitsForVisitorTest extends BaseIntegrationTest {

    @Autowired
    AllANCVisitsForVisitor allANCVisitsForVisitor;

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveANCVisit() {
        MRSUser staff = new MRSUser().person(new MRSPerson().firstName("hi")).systemId("systemId");
        MRSFacility mrsFacility = new MRSFacility("name", "country", "region", "county", "state");
        Facility facility = new Facility(mrsFacility).mrsFacilityId("mrsFacilityId");
        Patient mrsPatient = new Patient(new MRSPatient(null, new MRSPerson().addAttribute(new Attribute("attr", "value")), mrsFacility));

        allANCVisitsForVisitor.save(new ANCVisit().staff(staff).facility(facility).patient(mrsPatient)
                .date(new Date()).serialNumber("1234").visitNumber("1")
                .estDeliveryDate(new Date()).bpDiastolic(100).bpSystolic(60).pmtct("10")
                .weight(100.0).ttdose("2").iptdose("1").hemoglobin(8.0).dewormer("qwer")
                .iptReactive(false).itnUse("11").fhr(12).vdrlReactive("qwert").vdrlTreatment("qwerty")
                .fht(10.0).urineTestProteinPositive("q").urineTestGlucosePositive("qw")
                .preTestCounseled("qwe").hivTestResult("qwer").postTestCounseled("qwer")
                .pmtctTreament("a").location("as").house("aa").community("asd")
                .referred("he").maleInvolved(true).nextANCDate(new Date())
                .comments("comments").visitor(true));

        assertTrue("Should reach this line without any exception", true);
    }
}
