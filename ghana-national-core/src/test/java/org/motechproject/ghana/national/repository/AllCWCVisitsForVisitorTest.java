package org.motechproject.ghana.national.repository;

import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.CWCVisit;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AllCWCVisitsForVisitorTest extends BaseIntegrationTest {

    @Autowired
    AllCWCVisitsForVisitor allCWCVisitsForVisitor;

    @Test
    public void shouldSaveCWCVisit() {
        int initialSize = allCWCVisitsForVisitor.getAll().size();
        MRSUser mrsUser = new MRSUser().person(new MRSPerson().firstName("hi"));
        MRSFacility mrsFacility = new MRSFacility("name", "country", "region", "county", "state");
        Facility facility = new Facility(mrsFacility);
        allCWCVisitsForVisitor.add(new CWCVisit().staff(mrsUser).facility(facility).patient(null)
                .date(new Date()).serialNumber("1234").weight(100.0)
                .height(120.0).muac(1.).maleInvolved(true)
                .iptidose("1").pentadose("11").opvdose("2")
                .rotavirusdose("1").pneumococcaldose("1").cwcLocation("home")
                .comments("comments").house("house").visitor(true)
                .community("community").immunizations(new ArrayList<String>(){{
                    add("11");
                    add("22");
                }}));
        assertThat(allCWCVisitsForVisitor.getAll().size(), is(initialSize + 1));
    }
}
