package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.util.DateUtil.newDate;

public class ANCCareRegistrationTest {

    ANCCareRegistration ancCareRegistration;


    @Test
    public void shouldIncludeAllANCCaresOnRegistration() {
        LocalDate enrollmentDate = newDate(2012, 3, 3);
        LocalDate edd = newDate(2013, 1, 1);
        Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(edd);
        IPTVaccineCare mockIptVaccineCare = mock(IPTVaccineCare.class);
        TTVaccineCare mockTTVaccineCare = mock(TTVaccineCare.class);
        final String facilityId = "fid";

        Patient patient = new Patient(new MRSPatient("pid", "mid", null, new MRSFacility(facilityId)));
        ancCareRegistration = new ANCCareRegistration(mockTTVaccineCare, mockIptVaccineCare, patient, edd, enrollmentDate);

        PatientCare iptPatientCare = mock(PatientCare.class);
        PatientCare ttPatientCare = mock(PatientCare.class);
        when(mockIptVaccineCare.care(Matchers.<LocalDate>any())).thenReturn(iptPatientCare);
        when(mockTTVaccineCare.care()).thenReturn(ttPatientCare);

        List<PatientCare> patientCares = ancCareRegistration.allCares();
        //Delivery with date of conception as reference date
        assertThat(patientCares, is(asList(new PatientCare(ScheduleNames.ANC_DELIVERY, pregnancy.dateOfConception(), enrollmentDate, null, patient.facilityMetaData()), ttPatientCare, iptPatientCare)));

    }
}
