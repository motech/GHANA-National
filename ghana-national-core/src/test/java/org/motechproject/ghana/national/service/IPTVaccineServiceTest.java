package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;


public class IPTVaccineServiceTest extends BaseUnitTest {

    IPTVaccineService vaccinationService;
    @Mock
    ScheduleTrackingService mockScheduleTrackingService;

    @Before
    public void setUp() {
        initMocks(this);
        vaccinationService = new IPTVaccineService();
        setField(vaccinationService, "scheduleTrackingService", mockScheduleTrackingService);
    }

    @Test
    public void shouldEnrollForIPTpForIfCurrentPregnancyWeekIsOnOrBeforeWeek13() {

        String patientId = "patientId";
        Time deliveryTime = new Time(10, 10);
        DateTime todayAs6June2012 = new DateTime(2012, 6, 5, deliveryTime.getHour(), deliveryTime.getMinute());

        mockCurrentDate(todayAs6June2012);
        Patient patient = new Patient(new MRSPatient(patientId, "patientMotechId", new MRSPerson(), new MRSFacility("id")));

        Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(todayAs6June2012.plusWeeks(28).plusDays(6).toLocalDate());
        vaccinationService.enrollForPregnancyIPT(patient, pregnancy.dateOfDelivery());

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = forClass(EnrollmentRequest.class);
        verify(mockScheduleTrackingService).enroll(enrollmentRequestCaptor.capture());
        assertEnrollmentRequest(patientId, ANC_IPT_VACCINE, pregnancy.dateOfConception(), deliveryTime, enrollmentRequestCaptor.getValue());
    }

    @Test
    public void shouldNotEnrollForIPTpForIfCurrentPregnancyWeekIsAfterWeek13() {

        DateTime todayAs6June2012 = new DateTime(2012, 6, 5, 10, 10);
        mockCurrentDate(todayAs6June2012);
        vaccinationService.enrollForPregnancyIPT(mock(Patient.class), todayAs6June2012.plusWeeks(12).plusDays(6).toLocalDate());

        verify(mockScheduleTrackingService, never()).enroll(any(EnrollmentRequest.class));
    }

    private void assertEnrollmentRequest(String patientMotechId, String scheduleName, LocalDate referenceDate, Time preferredAlertTime, EnrollmentRequest actual) {
        assertThat(actual.getExternalId(), is(patientMotechId));
        assertThat(actual.getScheduleName(), is(scheduleName));
        assertThat(actual.getReferenceDate(), is(referenceDate));
        assertThat(actual.getPreferredAlertTime(), is(preferredAlertTime));

    }
}
