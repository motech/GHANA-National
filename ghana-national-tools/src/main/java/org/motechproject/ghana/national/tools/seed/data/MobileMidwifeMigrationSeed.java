package org.motechproject.ghana.national.tools.seed.data;

import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.MobileMidwifeSource;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.function.matcher.AndMatcher.and;
import static ch.lambdaj.function.matcher.OrMatcher.or;
import static org.hamcrest.CoreMatchers.equalTo;

@Component
public class MobileMidwifeMigrationSeed extends Seed {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MobileMidwifeSource mobileMidwifeSource;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    private static Map<String, String> FACILITY_CACHE = new HashMap<>();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void load() {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        List<MobileMidwifeEnrollment> mobileMidwifeEnrollments = mobileMidwifeSource.getPatients();
        List<Map<String, Object>> enrollments = mobileMidwifeEnrollmentData();
        List<MobileMidwifeEnrollment> nonVoiceEnrollments = filterDataForDirectSchedules(mobileMidwifeEnrollments);
        addMobileMidwifeEnrollmentDataToDB(nonVoiceEnrollments, enrollments, "abc");
//        List<MobileMidwifeEnrollment> voiceEnrollments = (List<MobileMidwifeEnrollment>) CollectionUtils.removeAll(mobileMidwifeEnrollments, nonVoiceEnrollments);
        mobileMidwifeEnrollments.removeAll(nonVoiceEnrollments);
        addMobileMidwifeEnrollmentDataToDB(migrateEnrollmentsForVoice(mobileMidwifeEnrollments), enrollments, "def");
    }

    List<MobileMidwifeEnrollment> migrateEnrollmentsForVoice(List<MobileMidwifeEnrollment> voiceEnrollments) {
        List<MobileMidwifeEnrollment> fixedEnrollments = new ArrayList<MobileMidwifeEnrollment>();

        List<MobileMidwifeEnrollment> validEnrollments = filter(and(having(on(MobileMidwifeEnrollment.class).getMedium(), equalTo(Medium.VOICE)),
                or(having(on(MobileMidwifeEnrollment.class).getPhoneOwnership(), equalTo(PhoneOwnership.HOUSEHOLD)), having(on(MobileMidwifeEnrollment.class).getPhoneOwnership(), equalTo(PhoneOwnership.PERSONAL)))),
                voiceEnrollments);
        for (MobileMidwifeEnrollment enrollment : validEnrollments) {
            if (enrollment.getDayOfWeek() == null)
                enrollment.setDayOfWeek(DayOfWeek.Sunday);
            if (enrollment.getTimeOfDay() == null)
                enrollment.setTimeOfDay(new Time(8, 0));
        }
        Group<MobileMidwifeEnrollment> midwifeEnrollmentGroup = group(validEnrollments, by(on(MobileMidwifeEnrollment.class).getDayOfWeek()), by(on(MobileMidwifeEnrollment.class).getTimeOfDay().getHour()));
        for (Group<MobileMidwifeEnrollment> groupedByDays : midwifeEnrollmentGroup.subgroups()) {
            for (Group<MobileMidwifeEnrollment> groupedByHour : groupedByDays.subgroups()) {
                int step = 0;
                List<MobileMidwifeEnrollment> mobileMidwifeEnrollments = groupedByHour.findAll();
                for (int i = 1; i <= mobileMidwifeEnrollments.size(); i++) {
                    MobileMidwifeEnrollment mobileMidwifeEnrollment = mobileMidwifeEnrollments.get(i - 1);
                    int hour = mobileMidwifeEnrollment.getTimeOfDay().getHour();
                    if (i % 119 == 0) {
                        hour += (i/120 + 1);
                        step = 0;
                    }
                    if (i % 20 == 0) step += 5;
                    Time timeOfDay = mobileMidwifeEnrollment.getTimeOfDay();
                    if (timeOfDay != null) {
                        mobileMidwifeEnrollment.setTimeOfDay(timeOfDay);
                        fixedEnrollments.add(mobileMidwifeEnrollment);
                        timeOfDay.setMinute(step);
                        timeOfDay.setHour(hour);
                    } else {
                        System.out.println("Invalid: " + mobileMidwifeEnrollment.getPatientId());
                    }
                }
            }
        }
        return fixedEnrollments;
    }

    List<MobileMidwifeEnrollment> filterDataForDirectSchedules(List<MobileMidwifeEnrollment> mobileMidwifeEnrollments) {
        return filter(or(having(on(MobileMidwifeEnrollment.class).getMedium(), equalTo(Medium.SMS)), having(on(MobileMidwifeEnrollment.class).getPhoneOwnership(), equalTo(PhoneOwnership.PUBLIC))),
                mobileMidwifeEnrollments);
    }

    private void addMobileMidwifeEnrollmentDataToDB(List<MobileMidwifeEnrollment> mobileMidwifeEnrollments, List<Map<String, Object>> enrollments, String x) {
        log.info("Calling again ****************************** " + x);
        MobileMidwifeEnrollment mobileMidwifeEnrollment = null;
        try {
            for (int i = 0; i < enrollments.size(); i++) {
                Map<String, Object> map = enrollments.get(i);
                String patientMotechId = (String) map.get("patient_motech_id");
                List<MobileMidwifeEnrollment> mobileMidwifeEnrollmentFiltered = filter(having(on(MobileMidwifeEnrollment.class).getPatientId(), equalTo(patientMotechId)), mobileMidwifeEnrollments);
                for (MobileMidwifeEnrollment enrollment : mobileMidwifeEnrollmentFiltered) {
                    mobileMidwifeEnrollment = enrollment;
                    if (mobileMidwifeEnrollment == null) {
                        log.warn("No enrollment record found for patient id :" + patientMotechId);
                        continue;
                    }
                    boolean active = i == 0;
                    Map<String, Object> earliestMessage = getEarliestMessage((String) map.get("enrollment_id"));
                    if (earliestMessage != null) {
                        String earliestMessageKey = (String) earliestMessage.get("message_key");
                        String[] splitMessageKey = StringUtils.split(earliestMessageKey, ".");
                        ServiceType serviceType = (earliestMessageKey.contains("child")) ? ServiceType.CHILD_CARE : ServiceType.PREGNANCY;
                        int startWeek = Integer.parseInt(splitMessageKey[2]);
                        int messageStartWeek = (serviceType.equals(ServiceType.CHILD_CARE)) ? (startWeek + 40) : startWeek;
                        if (messageStartWeek != 0) {
                            mobileMidwifeEnrollment.setMessageStartWeek(String.valueOf(messageStartWeek));
                            mobileMidwifeEnrollment = setEnrollmentDetails(mobileMidwifeEnrollment, patientMotechId, serviceType, active);
                            setFacility(mobileMidwifeEnrollment, map);
                            setStaffId(mobileMidwifeEnrollment, map);
                            mobileMidwifeService.register(mobileMidwifeEnrollment);
                            log.info("Migrated MobileMidwifeEnrollment for : " + mobileMidwifeEnrollment.getPatientId() + " in facility " + mobileMidwifeEnrollment.getFacilityId()
                                    + " under " + mobileMidwifeEnrollment.getServiceType().getDisplayName() + " campaign");
                        }
                    } else {
                        log.debug("No schedule to migrate for " + patientMotechId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred while adding data to DB for enrollment: " + mobileMidwifeEnrollment.toString(), e);
        }
    }

    private void setStaffId(MobileMidwifeEnrollment mobileMidwifeEnrollment, Map<String, Object> map) {
        String staffId = (String) map.get("systemId");
        String staffMotechId = (StringUtils.isNotBlank(staffId)) ? staffId : mobileMidwifeSource.getStaffIdFromPatientEncounter((String) map.get("patientId"));
        if (staffMotechId == null) {  //child clients who don't have *ANY* encounters.
            staffMotechId = mobileMidwifeSource.getMotechStaffIdForMotechFacilityId(mobileMidwifeEnrollment.getFacilityId());
        }
        mobileMidwifeEnrollment.setStaffId(staffMotechId);
    }

    private void setFacility(MobileMidwifeEnrollment mobileMidwifeEnrollment, Map<String, Object> map) {
        String facilityId = (StringUtils.isBlank((String) map.get("facilityId"))) ? (String) map.get("patient_facility_id") : (String) map.get("facilityId");
        if (facilityId.equals("33"))
            facilityId = "1"; //33 equals 'Ghana' country, not a location. hence mapped as 'unknown location'.

        String facilityMotechId;
        if (FACILITY_CACHE.get(facilityId) != null) {
            facilityMotechId = FACILITY_CACHE.get(facilityId);
        } else {
            facilityMotechId = mobileMidwifeSource.getMotechFacilityId(facilityId);
            FACILITY_CACHE.put(facilityId, facilityMotechId);
        }
        mobileMidwifeEnrollment.setFacilityId(facilityMotechId);
    }

    private MobileMidwifeEnrollment setEnrollmentDetails(MobileMidwifeEnrollment mobileMidwifeEnrollment, String patientMotechId, ServiceType serviceType, boolean active) {
        mobileMidwifeEnrollment = MobileMidwifeEnrollment.cloneNew(mobileMidwifeEnrollment);
        mobileMidwifeEnrollment.setServiceType(serviceType);
        mobileMidwifeEnrollment.setActive(active);
        mobileMidwifeEnrollment.setConsent(true);
        mobileMidwifeEnrollment.setPatientId(patientMotechId);
        return mobileMidwifeEnrollment;
    }

    private Map<String, Object> getEarliestMessage(String enrollmentId) {
        List<Map<String, Object>> messages = mobileMidwifeSource.scheduledMessage(enrollmentId);
        sortMessagesByDate(messages);
        return (!messages.isEmpty()) ? messages.get(0) : null;
    }

    private void sortEnrollmentsByDate(List<Map<String, Object>> newList) {
        Collections.sort(newList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                try {
                    return (dateFormat.parse((String) o1.get("startDate")).after(dateFormat.parse((String) o2.get("startDate")))) ? -1 : 1;
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
    }

    private void sortMessagesByDate(final List<Map<String, Object>> newList) {


        Collections.sort(newList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                try {
//                    log.info("**************************" + dateFormat.parse((String) o1.get("scheduled_date")) + "  ****  " + dateFormat.parse((String) o2.get("scheduled_date")));
                    return (dateFormat.parse((String) o1.get("scheduled_date")).after(dateFormat.parse((String) o2.get("scheduled_date")))) ? -1 : 1;
                } catch (ParseException e) {
                    return 0;
                } catch (RuntimeException re) {
                    log.info("**************************" + newList, re);
                    throw re;
                } catch (Throwable re) {
                    log.info("**************************" + newList, re);
                    throw re;
                }
            }
        });
    }

    private List<Map<String, Object>> mobileMidwifeEnrollmentData() {
        List<HashMap<String, String>> enrollmentData = mobileMidwifeSource.getEnrollmentData();
        List<Map<String, Object>> newListOfHashMaps = new ArrayList<>();
        for (HashMap<String, String> map : enrollmentData) {
            String patientId = map.get("patientId");
            final HashMap<String, Object> newHashMap = new HashMap<>();

            String patientMotechId = mobileMidwifeSource.getPatientMotechId(patientId);
            if (patientMotechId == null) {
                log.info("Patient does not have a MotechId: " + patientId);
                continue;  //patient has no motech id
            }
            newHashMap.put("patient_motech_id", patientMotechId);
            newHashMap.put("patient_facility_id", mobileMidwifeSource.getPatientFacility(patientId));

            if (StringUtils.isNotBlank(map.get("obs_id"))) { // Only Pregnant women will have observations captured.
                String observationId = map.get("obs_id");
                Map<String, String> locationProviderMap = mobileMidwifeSource.locationToProviderMapping(observationId);
                newHashMap.put("facilityId", locationProviderMap.get("location_id"));
                newHashMap.put("systemId", locationProviderMap.get("system_id"));
            }

            newHashMap.put("programName", map.get("programName"));
            newHashMap.put("startDate", map.get("startDate"));
            newHashMap.put("enrollment_id", map.get("enrollment_id"));
            newListOfHashMaps.add(newHashMap);
        }
        sortEnrollmentsByDate(newListOfHashMaps);
        return newListOfHashMaps;
    }
}
