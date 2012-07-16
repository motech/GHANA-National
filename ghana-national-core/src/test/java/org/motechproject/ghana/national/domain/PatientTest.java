package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.now;

public class PatientTest extends BaseUnitTest {

    DateTime todayAs6June2012;

    @Before
    public void setUp() {
        todayAs6June2012 = new DateTime(2012, 6, 5, 20, 10);
        mockCurrentDate(todayAs6June2012);
    }

    @Test
    public void shouldReturnPatientCareForIPTiForIfCurrentDOBIsOnOrBeforeWeek14() {

        DateTime birthDay = todayAs6June2012.minusWeeks(14).plusDays(1);
        String facilityId = "fid";
        Patient patient = patient(birthDay, facilityId);

        List<PatientCare> patientCares = patient
                .cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate(), new ArrayList<CwcCareHistory>(), noNewHistory(), new ActiveCareSchedules(), noNewHistory().getLastPentaDate(), noNewHistory().getLastIPTiDate(), null, null);
        assertThat(patientCares, hasItem(new PatientCare(CWC_IPT_VACCINE.getName(), birthDay.toLocalDate(), todayAs6June2012.toLocalDate(), null, facilityMetaData(facilityId))));

        birthDay = todayAs6June2012.minusWeeks(14);
        patientCares = patient(birthDay, facilityId)
                .cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate(), new ArrayList<CwcCareHistory>(), noNewHistory(), new ActiveCareSchedules(), noNewHistory().getLastPentaDate(), noNewHistory().getLastIPTiDate(), null, null);
        assertThat(patientCares, not(hasItem(new PatientCare(CWC_IPT_VACCINE.getName(), birthDay.toLocalDate(), todayAs6June2012.toLocalDate(), null, facilityMetaData(facilityId)))));
    }

    private CWCCareHistoryVO noNewHistory() {
        return new CWCCareHistoryVO();
    }

    @Test
    public void shouldReturnAllCWCCareProgramsApplicableDuringRegistrationIfThereIsNoHistory() {
        DateTime dateOfBirthWithTime = todayAs6June2012.minusMonths(1);
        String facilityId = "fid";
        Patient patient = patient(dateOfBirthWithTime, facilityId);
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate(), new ArrayList<CwcCareHistory>(), noNewHistory(), new ActiveCareSchedules(), noNewHistory().getLastPentaDate(), noNewHistory().getLastIPTiDate(), null, noNewHistory().getLastRotavirusDate());
        HashMap<String, String> metaData = facilityMetaData(facilityId);

        LocalDate expectedReferenceDate = dateOfBirthWithTime.toLocalDate();
        assertPatientCares(patientCares, asList(new PatientCare(CWC_BCG.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_YELLOW_FEVER.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_PENTA.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_ROTAVIRUS.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_MEASLES_VACCINE.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_IPT_VACCINE.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_OPV_0.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData),
                new PatientCare(CWC_OPV_OTHERS.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, metaData)));
    }

    @Test
    public void shouldNotReturnMeaslesPatientCareForCWCRegistration_IfAgeIsMoreThanAYear() {
        DateTime dateOfBirth5YearBack = todayAs6June2012.minusYears(5);
        String facilityId = "fid";
        Patient patient = patient(dateOfBirth5YearBack, facilityId);
        LocalDate expectedReferenceDate = dateOfBirth5YearBack.toLocalDate();
        assertThat(patient.cwcCareProgramToEnrollOnRegistration(todayAs6June2012.toLocalDate(),
                new ArrayList<CwcCareHistory>(), noNewHistory(), new ActiveCareSchedules(), noNewHistory().getLastPentaDate(), noNewHistory().getLastIPTiDate(), null, null), not(hasItem(new PatientCare(CWC_MEASLES_VACCINE.getName(), expectedReferenceDate, todayAs6June2012.toLocalDate(), null, facilityMetaData(facilityId)))));
    }

    @Test
    public void shouldNotReturnPatientCareIfHistoryIsRecordedForBcgYfMeasles(){
        DateTime birthdate = now();
        String facilityId = "fid";
        Patient patient = patient(birthdate, facilityId);
        LocalDate expectedReferenceDate = birthdate.toLocalDate();
        LocalDate enrollmentDate = expectedReferenceDate.plusWeeks(1);

        List<CwcCareHistory> cwcCareHistories = Arrays.asList(CwcCareHistory.BCG, CwcCareHistory.MEASLES, CwcCareHistory.YF);
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnHistoryCapture(enrollmentDate, cwcCareHistories, noNewHistory(), new ActiveCareSchedules(), noNewHistory().getLastPentaDate(), noNewHistory().getLastIPTiDate(), null);
        HashMap<String, String> metaData = facilityMetaData(facilityId);
        assertThat(patientCares,not(hasItem(new PatientCare(CWC_BCG.getName(), expectedReferenceDate,enrollmentDate, null, metaData))));
        assertThat(patientCares,not(hasItem(new PatientCare(CWC_YELLOW_FEVER.getName(), expectedReferenceDate,enrollmentDate, null, metaData))));
        assertThat(patientCares,not(hasItem(new PatientCare(CWC_MEASLES_VACCINE.getName(), expectedReferenceDate,enrollmentDate, null, metaData))));

    }

    @Test
    public void shouldReturnPatientCaresForPNCChildProgram() {
        DateTime dateOfBirth = todayAs6June2012.minusDays(5);
        String facilityId = "fid";
        Patient patient = patient(dateOfBirth, facilityId);
        List<PatientCare> cares = patient.pncBabyProgramsToEnrollOnRegistration();
        HashMap<String, String> metaData = facilityMetaData(facilityId);
        assertPatientCares(cares, asList(
                new PatientCare(PNC_CHILD_1.getName(), dateOfBirth, dateOfBirth,null,metaData),
                new PatientCare(PNC_CHILD_2.getName(), dateOfBirth, dateOfBirth,null,metaData),
                new PatientCare(PNC_CHILD_3.getName(), dateOfBirth, dateOfBirth,null,metaData)));
    }

    @Test
    public void shouldReturnPatientCaresForPNCMotherProgram() {
        DateTime deliveryDate = todayAs6June2012.minusDays(10);
        String facilityId = "fid";
        Patient patient = patient(todayAs6June2012, facilityId);
        List<PatientCare> cares = patient.pncMotherProgramsToEnrollOnRegistration(deliveryDate);
        HashMap<String, String> metaData = facilityMetaData(facilityId);
        assertPatientCares(cares, asList(
                new PatientCare(PNC_MOTHER_1.getName(), deliveryDate, deliveryDate,null,metaData),
                new PatientCare(PNC_MOTHER_2.getName(), deliveryDate, deliveryDate,null,metaData),
                new PatientCare(PNC_MOTHER_3.getName(), deliveryDate, deliveryDate,null,metaData)));
    }


    @Test
    public void shouldReturnMetaDataMapForPatient() {
        String facilityId = "facilityId";
        Patient patient = new Patient(new MRSPatient("patientId", "mId", null, new MRSFacility(facilityId)));
        Map expectedMap = new HashMap<String, String>();
        expectedMap.put(Patient.FACILITY_META, facilityId);
        assertThat(patient.facilityMetaData(), is(expectedMap));
    }

    @Test
    public void shouldReturnAllApplicableCaresBasedOnHistoryInput(){
        DateTime birthdate = now();
        String facilityId = "fid";
        Patient patient = patient(birthdate, facilityId);
        LocalDate expectedReferenceDate = birthdate.toLocalDate();
        LocalDate enrollmentDate = expectedReferenceDate.plusWeeks(1);
        List<CwcCareHistory> cwcCareHistories = Arrays.asList(CwcCareHistory.PENTA);
        Date lastPentaDate = birthdate.plusWeeks(4).toDate();
        Date lastIPTiDate = birthdate.plusWeeks(10).toDate();
        //TODO:handle rotavirus when history story is played
        CWCCareHistoryVO cwcCareHistoryVO = new CWCCareHistoryVO(true, cwcCareHistories, null, null, null, null, lastPentaDate, 1, null, null, 1, lastIPTiDate, null, null);
        HashMap<String, String> metaData = facilityMetaData(facilityId);
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnHistoryCapture(enrollmentDate, cwcCareHistories, cwcCareHistoryVO, new ActiveCareSchedules(), lastPentaDate,lastIPTiDate,null);
        assertThat(patientCares,hasItem(new PatientCare(CWC_PENTA.getName(),null,newDate(lastPentaDate),PentaDose.PENTA2.milestoneName(),metaData)));
        assertThat(patientCares,hasItem(new PatientCare(CWC_IPT_VACCINE.getName(),null,newDate(lastIPTiDate), IPTiDose.IPTi2.milestoneName(),metaData)));
    }

    @Test
    public void shouldReturnPhoneNumberRegisteredWithMobileMidwifeEnrollmentIfAnyOtherwiseReturnPatientPhoneNumber(){
        String phoneNumber = "919500012123";
        String mobileMidwifePhoneNumber = "919544412111";
        Patient patient = new Patient(new MRSPatient("motechiD", new MRSPerson().attributes(Arrays.asList(new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), phoneNumber))), new MRSFacility("facilityid")));
        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(DateTime.now());
        mobileMidwifeEnrollment.setPhoneNumber(mobileMidwifePhoneNumber);
        assertThat(patient.receiveSMSOnPhoneNumber(mobileMidwifeEnrollment), is(equalTo(mobileMidwifePhoneNumber)));
        assertThat(patient.receiveSMSOnPhoneNumber(null), is(equalTo(phoneNumber)));
    }

    private void assertPatientCares(List<PatientCare> actualList, List<PatientCare> expectedList) {
        assertThat(actualList.size(), is(expectedList.size()));
        for (PatientCare expectedCare : expectedList)
            assertThat("Missing " + expectedCare.name(), actualList, hasItem(expectedCare));
    }

    private Patient patient(DateTime birthDay, String facilityId) {
        return new Patient(new MRSPatient(null, new MRSPerson().dateOfBirth(birthDay.toDate()), new MRSFacility(facilityId, "fname", "fcountry", "fregion", "fcountry", "state")));
    }

    public static Patient createPatient(String patientId, String motechId, LocalDate dob, String facilityId) {
        return new Patient(new MRSPatient(patientId, motechId, new MRSPerson().dateOfBirth(dob.toDate()), new MRSFacility(facilityId)));
    }

    public static HashMap<String, String> facilityMetaData(final String facilityId) {
        return new HashMap<String, String>(){{
            put(Patient.FACILITY_META, facilityId);
        }};
    }
}
