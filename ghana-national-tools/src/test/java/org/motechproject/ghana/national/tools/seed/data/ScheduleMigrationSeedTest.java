package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.Filter;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.tools.seed.data.factory.TestUpcomingSchedule.newUpcomingSchedule;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-tools.xml"})
public class ScheduleMigrationSeedTest {
    @Autowired
    AllTrackedSchedules allTrackedSchedules;
    @Mock
    private AllSchedules allSchedules;

    private TTVaccineSeed ttVaccineSeed;

    private IPTIVaccineSeed iptiVaccineSeed;

    @Mock
    private OldGhanaScheduleSource oldGhanaScheduleSource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineSeed = new TTVaccineSeed(oldGhanaScheduleSource, allTrackedSchedules, allSchedules);
        iptiVaccineSeed = new IPTIVaccineSeed(oldGhanaScheduleSource, allTrackedSchedules, allSchedules);
    }

    @Test
    public void shouldApplyFiltersToEliminateInvalidScheduleRecords(){
        Filter filter = mock(Filter.class);
        ttVaccineSeed.filters = Arrays.asList(filter);
        final List<UpcomingSchedule> upcomingSchedules = Arrays.asList(newUpcomingSchedule("10000", "2012-9-22 10:30:00.0", "TT3").build());
        when(filter.filter(upcomingSchedules)).thenReturn(upcomingSchedules);
        ttVaccineSeed.migrate(upcomingSchedules);
        verify(filter).filter(upcomingSchedules);
    }

    @Test
    public void shouldEnrollForScheduleGivenDueDateOfVaccination() {
        String patientId1 = "101";
        String patientId2 = "102";
        List<UpcomingSchedule> upcomingSchedulesFromDb = Arrays.asList(newUpcomingSchedule(patientId1, "2012-9-22 10:30:00.0", "TT3").build(),
                newUpcomingSchedule(patientId2, "2012-2-29 10:30:00.0", "TT2").build());
        List<TTVaccine> expectedTTVaccines = Arrays.asList(
                new TTVaccine(newDateTime(2012, 3, 22, new Time(10, 30)), TTVaccineDosage.TT3, new Patient(new MRSPatient(patientId1))),
                new TTVaccine(newDateTime(2012, 2, 1, new Time(10, 30)), TTVaccineDosage.TT2, new Patient(new MRSPatient(patientId2))));

        ttVaccineSeed.migrate(upcomingSchedulesFromDb);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules, times(2)).enroll(captor.capture());
        final List<EnrollmentRequest> enrollmentRequests = captor.getAllValues();
        assertTTEnrollmentRequest(enrollmentRequests.get(0), expectedTTVaccines.get(0).getVaccinationDate(), "TT3", patientId1, expectedTTVaccines.get(0).getVaccinationDate());
        assertTTEnrollmentRequest(enrollmentRequests.get(1), expectedTTVaccines.get(1).getVaccinationDate(), "TT2", patientId2, expectedTTVaccines.get(1).getVaccinationDate());
    }

    @Test
    public void shouldLoadAllUpcomingSchedules() {
        final TTVaccineSeed ttVaccineSeedSpy = spy(ttVaccineSeed);
        List<UpcomingSchedule> upcomingSchedules = Arrays.asList(mock(UpcomingSchedule.class));
        when(ttVaccineSeedSpy.getAllUpcomingSchedules()).thenReturn(upcomingSchedules);
        doNothing().when(ttVaccineSeedSpy).migrate(Matchers.<List<UpcomingSchedule>>any());
        ttVaccineSeedSpy.load();
        verify(ttVaccineSeedSpy).migrate(upcomingSchedules);
    }

    @Test
    public void shouldCalculateReferenceDateForTheMileStoneFromTheDueDate() {
        DateTime expectedReferenceDate = newDateTime(DateUtil.newDate(2012, 2, 1), new Time(10, 10));
        UpcomingSchedule upcomingSchedule = newUpcomingSchedule("100", "2012-2-29 10:10:00.0", "TT2").build();
        assertThat(ttVaccineSeed.getReferenceDate(upcomingSchedule), is(expectedReferenceDate));
    }

    @Test
    public void shouldEnrollIntoIPTIVaccineSchedule(){
        DateTime referenceDate = DateUtil.newDateTime(2012, 1, 1, new Time(10, 10));
        Patient patient = new Patient(new MRSPatient("10000"));

        iptiVaccineSeed.enroll(referenceDate, "IPTI1", patient);

        ArgumentCaptor<EnrollmentRequest> enrollmentCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules).enroll(enrollmentCaptor.capture());

        assertTTEnrollmentRequest(enrollmentCaptor.getValue(), referenceDate.toDateTime(), "IPT1", "10000", referenceDate.toDateTime());
    }


    public static void assertTTEnrollmentRequest(EnrollmentRequest enrollmentRequest, DateTime referenceDateTime, String milestoneName, String externalId, DateTime enrollmentDateTime) {
        assertThat(enrollmentRequest.getReferenceDateTime(), is(equalTo(referenceDateTime)));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(milestoneName)));
        assertThat(enrollmentRequest.getExternalId(), is(equalTo(externalId)));
        assertThat(enrollmentRequest.getEnrollmentDateTime(), is(equalTo(enrollmentDateTime)));
    }

}
