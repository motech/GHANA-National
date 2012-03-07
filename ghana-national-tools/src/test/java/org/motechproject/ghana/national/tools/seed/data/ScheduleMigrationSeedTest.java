package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.TTVaccineSource;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-tools.xml"})
public class ScheduleMigrationSeedTest {
    @Autowired
    AllTrackedSchedules allTrackedSchedules;
    @Mock
    private AllSchedules allSchedules;

    private TTVaccineSeed ttVaccineSeed;

    @Mock
    private TTVaccineSource ttVaccineSource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineSeed = new TTVaccineSeed(ttVaccineSource, allTrackedSchedules, allSchedules);
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionIfAPatientHaveMoreThanOneActiveUpcomingScheduleEntry() {
        List<UpcomingSchedule> upcomingSchedulesFromDb = Arrays.asList(new UpcomingSchedule("100", "2012-12-17 00:00:00.0", "TT2"), new UpcomingSchedule("100", "2012-12-21 00:00:00.0", "TT2"), new UpcomingSchedule("101", "2012-12-21 00:00:00.0", "TT3"));
        ttVaccineSeed.migrate(upcomingSchedulesFromDb);
    }

    @Test
    public void shouldEnrollForScheduleGivenDueDateOfVaccination() {
        String patientId1 = "101";
        String patientId2 = "102";
        List<UpcomingSchedule> upcomingSchedulesFromDb = Arrays.asList(new UpcomingSchedule(patientId1, "2012-9-22 10:30:00.0", "TT3"), new UpcomingSchedule(patientId2, "2012-2-29 10:30:00.0", "TT2"));
        List<TTVaccine> expectedTTVaccines = Arrays.asList(
                new TTVaccine(DateUtil.newDateTime(2012, 3, 22, new Time(10, 30)), TTVaccineDosage.TT3, new Patient(new MRSPatient(patientId1))),
                new TTVaccine(DateUtil.newDateTime(2012, 2, 1, new Time(10, 30)), TTVaccineDosage.TT2, new Patient(new MRSPatient(patientId2))));

        ttVaccineSeed.migrate(upcomingSchedulesFromDb);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(allSchedules, times(2)).enroll(captor.capture());
        final List<EnrollmentRequest> enrollmentRequests = captor.getAllValues();
        TTVaccineSeedTest.assertTTEnrollmentRequest(enrollmentRequests.get(0), expectedTTVaccines.get(0).getVaccinationDate(), "TT3", patientId1);
        TTVaccineSeedTest.assertTTEnrollmentRequest(enrollmentRequests.get(1), expectedTTVaccines.get(1).getVaccinationDate(), "TT2", patientId2);

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
        DateTime expectedReferenceDate = DateUtil.newDateTime(DateUtil.newDate(2012, 2, 1), new Time(10, 10));
        UpcomingSchedule upcomingSchedule = new UpcomingSchedule("100", "2012-2-29 10:10:00.0", "TT2");
        assertThat(ttVaccineSeed.getReferenceDate(upcomingSchedule), is(expectedReferenceDate));
    }
}
