package org.motechproject.ghana.national.tools.seed.data;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.MobileMidwifeSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MobileMidwifeMigrationSeed extends Seed {

    private static Logger LOG = Logger.getLogger(MobileMidwifeMigrationSeed.class);

    @Autowired
    MobileMidwifeSource mobileMidwifeSource;

    @Autowired
    AllMobileMidwifeEnrollments allMobileMidwifeEnrollments;

    private static Map<String, String> FACILITY_CACHE = new HashMap<String, String>();

    @Override
    protected void load() {
        List<MobileMidwifeEnrollment> mobileMidwifeEnrollments = mobileMidwifeSource.getPatients();
        List<HashMap<String, String>> enrollmentData = mobileMidwifeSource.getEnrollmentData();
        List<Map<String, Object>> newListOfHashMaps = mobileMidwifeEnrollmentData(enrollmentData);

        for (MobileMidwifeEnrollment mobileMidwifeEnrollment : mobileMidwifeEnrollments) {
            final List<Map<String, Object>> enrollments = new ArrayList<Map<String, Object>>();
            String patientMotechId = mobileMidwifeEnrollment.getPatientId();
            for (Map<String, Object> map : newListOfHashMaps) {
                final String enrollmentPatientMotechId = (String) map.get("patient_motech_id");
                if (enrollmentPatientMotechId != null && enrollmentPatientMotechId.equals(patientMotechId)) {
                    enrollments.add(map);
                }
            }
            sortEnrollmentsByDate(enrollments);
            addMobileMidwifeEnrollmentDataToDB(mobileMidwifeEnrollment, enrollments);
        }
    }

    private void addMobileMidwifeEnrollmentDataToDB(MobileMidwifeEnrollment mobileMidwifeEnrollment, List<Map<String, Object>> enrollments) {
        for (int i = 0; i < enrollments.size(); i++) {
            String patientMotechId = (String) enrollments.get(i).get("patient_motech_id");
            ServiceType serviceType = (((String) enrollments.get(i).get("programName")).contains("Pregnancy")) ? ServiceType.PREGNANCY : ServiceType.CHILD_CARE;
            boolean active = i == 0;
            mobileMidwifeEnrollment = setEnrollmentDetails(mobileMidwifeEnrollment, patientMotechId, serviceType, active);
            setFacility(mobileMidwifeEnrollment, enrollments.get(i));
            setStaffId(mobileMidwifeEnrollment, enrollments.get(i));
            allMobileMidwifeEnrollments.add(mobileMidwifeEnrollment);
            LOG.info("Migrated MobileMidwifeEnrollment for : " + mobileMidwifeEnrollment.getPatientId() + " in facility " + mobileMidwifeEnrollment.getFacilityId() + " under " + mobileMidwifeEnrollment.getServiceType().getDisplayName() + " campaign");
        }
    }

    private void setStaffId(MobileMidwifeEnrollment mobileMidwifeEnrollment, Map<String, Object> map) {
        String staffMotechId;
        String staffId = (String) map.get("systemId");
        if (StringUtils.isNotBlank(staffId)) {
            staffMotechId = staffId;
        } else {
            staffMotechId = mobileMidwifeSource.getStaffIdFromPatientEncounter((String) map.get("patientId"));
        }
        if (staffMotechId == null) {
            //child clients who don't have *ANY* encounters.
            staffMotechId = mobileMidwifeSource.getMotechStaffIdForMotechFacilityId(mobileMidwifeEnrollment.getFacilityId());
        }
        mobileMidwifeEnrollment.setStaffId(staffMotechId);
    }

    private void setFacility(MobileMidwifeEnrollment mobileMidwifeEnrollment, Map<String, Object> map) {
        String facilityId;
        if (StringUtils.isBlank((String) map.get("facilityId"))) {
            facilityId = (String) map.get("patient_facility_id");
        } else {
            facilityId = (String) map.get("facilityId");
        }

        if (facilityId.equals("33")) facilityId = "1"; //33 equals 'Ghana' country, so its an unknown location.

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
        mobileMidwifeEnrollment.setMessageStartWeek("1");
        mobileMidwifeEnrollment.setActive(active);
        mobileMidwifeEnrollment.setConsent(true);
        mobileMidwifeEnrollment.setPatientId(patientMotechId);
        return mobileMidwifeEnrollment;
    }

    private void sortEnrollmentsByDate(List<Map<String, Object>> newList) {
        Collections.sort(newList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                try {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    return (dateFormat.parse((String) o1.get("startDate")).after(dateFormat.parse((String) o2.get("startDate")))) ? -1 : 1;
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
    }

    private List<Map<String, Object>> mobileMidwifeEnrollmentData(List<HashMap<String, String>> enrollmentData) {
        List<Map<String, Object>> newListOfHashMaps = new ArrayList<Map<String, Object>>();
        for (HashMap<String, String> map : enrollmentData) {
            String patientId = map.get("patientId");
            final HashMap<String, Object> newHashMap = new HashMap<String, Object>();

            String patientMotechId = mobileMidwifeSource.getPatientMotechId(patientId);
            if (patientMotechId == null)    {
                LOG.info("Patient does not have a MotechId: " + patientId);
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
            newListOfHashMaps.add(newHashMap);
        }
        return newListOfHashMaps;
    }
}
