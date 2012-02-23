package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT1;

public class TTVaccinationVisitEncounterFactoryTest {

    @Test
    public void shouldCreateEncounterForTTVisit(){
        MRSUser staff = new MRSUser();
        String facilityId = "facility id";
        Facility facility = new Facility(new MRSFacility(facilityId));
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final LocalDate vaccinationDate = DateUtil.newDate(2000, 2, 1);

        TTVisit ttVisit = new TTVisit().dosage(TT1).patient(patient).staff(staff).facility(facility).date(vaccinationDate.toDate());
        Encounter encounterForVisit = new TTVaccinationVisitEncounterFactory().createEncounterForVisit(ttVisit);    
        
        assertThat(encounterForVisit.getMrsPatient(), is(patient.getMrsPatient()));
        assertThat(encounterForVisit.getDate(), is(vaccinationDate.toDate()));
        assertThat(encounterForVisit.getFacility(), is(facility.mrsFacility()));
        assertThat(encounterForVisit.getType(), is(EncounterType.TT_VISIT.value()));
        assertThat(encounterForVisit.getStaff(), is(staff));
    }
}
