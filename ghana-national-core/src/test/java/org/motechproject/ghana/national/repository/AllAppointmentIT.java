package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.testing.utils.BaseUnitTest;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.motechproject.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class AllAppointmentIT extends BaseUnitTest {

    @Autowired
    AllAppointments allAppointments;

    Patient patient1 = new Patient(new MRSPatient("motechId1", null, null));
    Patient patient2 = new Patient(new MRSPatient("motechId2", null, null));

    @Test
    public void testSearchForAppointmentsForCurrentWeek() {

        DateTime now = now();
        DateTime dueDateIn6Days = now.plusDays(6);
        DateTime dueDateInPast = now.minusDays(1);
        DateTime dueDate2In7Days = now.plusDays(7);

        allAppointments.updateANCVisitSchedule(patient1, dueDateIn6Days);
        allAppointments.updateANCVisitSchedule(patient1, dueDateInPast);
        allAppointments.updateANCVisitSchedule(patient2, dueDate2In7Days);
        allAppointments.updateANCVisitSchedule(patient2, now.withTimeAtStartOfDay());

        List<VisitResponse> responsesForPatient1 = allAppointments.upcomingAppointmentsForCurrentWeek(patient1.getMotechId());
        List<VisitResponse> responsesForPatient2 = allAppointments.upcomingAppointmentsForCurrentWeek(patient2.getMotechId());

        assertThat(responsesForPatient1.size(), is(1));
        assertThat(responsesForPatient2.size(), is(1));
        assertVisitResponse(responsesForPatient1.get(0), dueDateIn6Days, patient1.getMotechId());
        assertVisitResponse(responsesForPatient2.get(0), now.withTimeAtStartOfDay(), patient2.getMotechId());
    }

    private void assertVisitResponse(VisitResponse response, DateTime dueDate , String motechId) {
        assertThat(response.getExternalId(), is(motechId));
        assertThat(response.getAppointmentDueDate(), is(dueDate));
    }

    @After
    public void clearAll() throws SchedulerException {
        allAppointments.remove(patient1);
        allAppointments.remove(patient2);
    }
    }
