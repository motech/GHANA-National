package org.motechproject.ghana.national.tools.seed.data;

import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;

public class MobileMidwifeMigrationSeedTest {
    @Test
    public void shouldFilterListForNonVoiceEnrollments() {
        ArrayList<MobileMidwifeEnrollment> enrollments = new ArrayList<>();
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PUBLIC));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setMedium(Medium.SMS));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setMedium(Medium.SMS));

        List<MobileMidwifeEnrollment> result = new MobileMidwifeMigrationSeed().filterDataForDirectSchedules(enrollments);
        assertEquals(3, result.size());
    }

    @Test
    public void shouldGroupListByDayOfWeekAndHour() {
        ArrayList<MobileMidwifeEnrollment> enrollments = new ArrayList<>();
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Sunday).setTimeOfDay(Time.parseTime("12:12",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:43",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Monday).setTimeOfDay(Time.parseTime("11:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Tuesday).setTimeOfDay(Time.parseTime("8:16",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Tuesday).setTimeOfDay(Time.parseTime("8:04",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Saturday).setTimeOfDay(Time.parseTime("11:22",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Friday).setTimeOfDay(Time.parseTime("7:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));
        enrollments.add(new MobileMidwifeEnrollment(DateTime.now()).setDayOfWeek(DayOfWeek.Thursday).setTimeOfDay(Time.parseTime("6:00",":")).setMedium(Medium.VOICE).setPhoneOwnership(PhoneOwnership.PERSONAL));

        List<MobileMidwifeEnrollment> result = new MobileMidwifeMigrationSeed().migrateEnrollmentsForVoice(enrollments);
        assertEquals(46, result.size());
        assertEquals(20, Lambda.filter(having(on(MobileMidwifeEnrollment.class).getTimeOfDay(), equalTo(Time.parseTime("11:00", ":"))), result).size());
        assertEquals(20, Lambda.filter(having(on(MobileMidwifeEnrollment.class).getTimeOfDay(), equalTo(Time.parseTime("11:05", ":"))), result).size());
        assertEquals(1, Lambda.filter(having(on(MobileMidwifeEnrollment.class).getTimeOfDay(), equalTo(Time.parseTime("11:10", ":"))), result).size());
    }
}
