package org.motechproject.ghana.national.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PNCMotherRequestMapperTest {

    private PNCMotherRequestMapper pncMotherRequestMapper = new PNCMotherRequestMapper();

    @Test
    public void shouldSavePNCMotherRequest() {
        String comment = "comment";
        String community = "community";
        DateTime date = DateUtil.now();
        String facilityId = "facilityId";
        String fht = "120";
        String house = "house";
        String location = "location";
        Boolean lochiaAmountExcess = Boolean.TRUE;
        String lochiaColour = "1";
        Boolean lochiaOdourFoul = Boolean.TRUE;
        Boolean maleInvolved = Boolean.TRUE;
        String motechId = "motechId";
        double temperature = 20D;
        int visitNumber = 1;
        String vitaminA = "vitaminA";
        String staffId = "staffId";
        Boolean referred = Boolean.TRUE;
        String ttDose = "1";

        Facility facility = new Facility(new MRSFacility(facilityId));
        Patient patient = mock(Patient.class);
        MRSUser mrsUser = mock(MRSUser.class);

        final PNCMotherForm pncMotherForm = createPNCMotherForm(comment, community, date, facilityId, staffId,
                fht, house, location, lochiaAmountExcess, lochiaColour, lochiaOdourFoul, maleInvolved, motechId, temperature, visitNumber, vitaminA);
        pncMotherForm.setReferred(referred);
        pncMotherForm.setTtDose(ttDose);

        PNCMotherRequest pncMotherRequest = pncMotherRequestMapper.map(pncMotherForm, patient, mrsUser, facility);

        assertThat(pncMotherRequest.getDate(), is(date));
        assertThat(pncMotherRequest.getFacility(), is(facility));
        assertThat(pncMotherRequest.getPatient(), is(patient));
        assertThat(pncMotherRequest.getStaff(), is(mrsUser));
        assertThat(pncMotherRequest.getComments(), is(comment));
        assertThat(pncMotherRequest.getCommunity(), is(community));
        assertThat(pncMotherRequest.getFht(), is(fht));
        assertThat(pncMotherRequest.getHouse(), is(house));
        assertThat(pncMotherRequest.getLocation(), is(location));
        assertThat(pncMotherRequest.getLochiaAmountExcess(), is(lochiaAmountExcess));
        assertThat(pncMotherRequest.getLochiaColour(), is(lochiaColour));
        assertThat(pncMotherRequest.getLochiaOdourFoul(), is(lochiaOdourFoul));
        assertThat(pncMotherRequest.getMaleInvolved(), is(maleInvolved));
        assertThat(pncMotherRequest.getReferred(), is(referred));
        assertThat(pncMotherRequest.getTemperature(), is(temperature));
        assertThat(pncMotherRequest.getTtDose(), is(ttDose));
        assertThat(pncMotherRequest.getVisit().visitNumber(), is(visitNumber));
        assertThat(pncMotherRequest.getVitaminA(), is(vitaminA));
    }

    private PNCMotherForm createPNCMotherForm(String comment, String community, DateTime date, String facilityId, String staffId, String fht, String house, String location, Boolean lochiaAmountExcess, String lochiaColour,
                                              Boolean lochiaOdourFoul, Boolean maleInvolved, String motechId, double temperature, int visitNumber, String vitaminA) {
        final PNCMotherForm pncMotherForm = new PNCMotherForm();
        pncMotherForm.setComments(comment);
        pncMotherForm.setCommunity(community);
        pncMotherForm.setDate(date);
        pncMotherForm.setFacilityId(facilityId);
        pncMotherForm.setFht(fht);
        pncMotherForm.setHouse(house);
        pncMotherForm.setLocation(location);
        pncMotherForm.setLochiaAmountExcess(lochiaAmountExcess);
        pncMotherForm.setLochiaColour(lochiaColour);
        pncMotherForm.setLochiaOdourFoul(lochiaOdourFoul);
        pncMotherForm.setStaffId(staffId);
        pncMotherForm.setMaleInvolved(maleInvolved);
        pncMotherForm.setReferred(Boolean.TRUE);
        pncMotherForm.setMotechId(motechId);
        pncMotherForm.setTemperature(temperature);
        pncMotherForm.setVisitNumber(visitNumber);
        pncMotherForm.setVitaminA(vitaminA);
        return pncMotherForm;
    }
}
