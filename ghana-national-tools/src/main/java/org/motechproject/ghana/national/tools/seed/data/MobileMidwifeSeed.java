package org.motechproject.ghana.national.tools.seed.data;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.MobileMidwifeSource;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MobileMidwifeSeed extends Seed {

    @Autowired
    MobileMidwifeSource mobileMidwifeSource;

    @Autowired
    AllMobileMidwifeEnrollments allMobileMidwifeEnrollments;

    @Autowired
    AllPatients allPatients;

    @Autowired
    AllFacilities allFacilities;

    private static Map<String, String> FACILITY_CACHE = new HashMap<String, String>();

    @Override
    protected void load() {
        List<MobileMidwifeEnrollment> mobileMidwifeEnrollments = mobileMidwifeSource.getPatients();
        List<HashMap<String, String>> enrollmentData = mobileMidwifeSource.getEnrollmentData();
        List<Map<String, Object>> newListOfHashMaps = mobileMidwifeEnrollmentData(enrollmentData);

        for (MobileMidwifeEnrollment mobileMidwifeEnrollment : mobileMidwifeEnrollments) {
            String patientId = mobileMidwifeEnrollment.getPatientId();
            final List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map : newListOfHashMaps) {
                final Patient patient = (Patient) map.get("patient");
                if (patient != null && patient.getMrsPatient().getMotechId().equals(patientId)) {
                    newList.add(map);
                }
            }
            sortEnrollmentsByDate(newList);
            addMobileMidwifeEnrollmentDataToDB(mobileMidwifeEnrollment, newList);

        }
    }

    private void addMobileMidwifeEnrollmentDataToDB(MobileMidwifeEnrollment mobileMidwifeEnrollment, List<Map<String, Object>> newList) {
        for (int i = 0; i < newList.size(); i++) {
            Patient patient = (Patient) newList.get(i).get("patient");
            ServiceType serviceType = (((String) newList.get(i).get("programName")).contains("Pregnancy")) ? ServiceType.PREGNANCY : ServiceType.CHILD_CARE;
            boolean active = i == 0;
            mobileMidwifeEnrollment = setEnrollmentDetails(mobileMidwifeEnrollment, patient, serviceType, active);
            setFacility(mobileMidwifeEnrollment, patient, newList.get(i));
            setStaffId(mobileMidwifeEnrollment, patient, newList.get(i));

        }
    }

    private void setStaffId(MobileMidwifeEnrollment mobileMidwifeEnrollment, Patient patient, Map<String, Object> map) {
        String staffMotechId;
        String patientId = patient.getMrsPatient().getId();
        String staffId = (String) map.get("systemId");
        if (StringUtils.isNotBlank(staffId)) {
            staffMotechId = staffId;
        } else {
            staffMotechId = mobileMidwifeSource.getStaffIdFromPatientEncounter(patientId);
        }
        if(staffMotechId == null) {
            //child clients who don't have *ANY* encounters.
            staffMotechId = mobileMidwifeSource.getMotechStaffIdForMotechFacilityId(mobileMidwifeEnrollment.getFacilityId());
        }
        mobileMidwifeEnrollment.setStaffId(staffMotechId);
        allMobileMidwifeEnrollments.add(mobileMidwifeEnrollment);
    }

    private void setFacility(MobileMidwifeEnrollment mobileMidwifeEnrollment, Patient patient, Map<String, Object> map) {
        String facilityId;
        if (StringUtils.isBlank((String) map.get("facilityId"))) {
            if (patient.getMrsPatient().getFacility() == null) {
                facilityId = "1"; //unknown location
            } else {
                facilityId = patient.getMrsPatient().getFacility().getId();
            }
        } else {
            facilityId = (String) map.get("facilityId");
            if (facilityId.equals("33")) facilityId = "1"; //33 equals 'Ghana' country, so its an unknown location.
        }

        String facilityMotechId;
        if (FACILITY_CACHE.get(facilityId) != null) {
            facilityMotechId = FACILITY_CACHE.get(facilityId);
        } else {
            facilityMotechId = allFacilities.findByMrsFacilityId(facilityId).getMotechId();
            FACILITY_CACHE.put(facilityId, facilityMotechId);
        }
        mobileMidwifeEnrollment.setFacilityId(facilityMotechId);
    }

    private MobileMidwifeEnrollment setEnrollmentDetails(MobileMidwifeEnrollment mobileMidwifeEnrollment, Patient patient, ServiceType serviceType, boolean active) {
        mobileMidwifeEnrollment = MobileMidwifeEnrollment.cloneNew(mobileMidwifeEnrollment);
        mobileMidwifeEnrollment.setServiceType(serviceType);
        mobileMidwifeEnrollment.setMessageStartWeek("1");
        mobileMidwifeEnrollment.setActive(active);
        mobileMidwifeEnrollment.setConsent(true);
        mobileMidwifeEnrollment.setPatientId(patient.getMrsPatient().getMotechId());
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

            Patient patient = allPatients.patientByOpenmrsId(patientId);
            if (patient == null) continue;  //patient has no motech id
            if (patient.getMrsPatient().getMotechId() == null) {  //patient is voided. can't get details from api, hence direct sql call.
                patient = new Patient(new MRSPatient(patientId, mobileMidwifeSource.getPatientMotechId(patientId), null, patient.getMrsPatient().getFacility()));
            }

            if (StringUtils.isNotBlank(map.get("obs_id"))) {
                String observationId = map.get("obs_id");
                Map<String, String> locationProviderMap = mobileMidwifeSource.locationToProviderMapping(observationId);
                newHashMap.put("facilityId", locationProviderMap.get("location_id"));
                newHashMap.put("systemId", locationProviderMap.get("system_id"));
            }

            newHashMap.put("patient", patient);
            newHashMap.put("programName", map.get("programName"));
            newHashMap.put("startDate", map.get("startDate"));
            newListOfHashMaps.add(newHashMap);
        }
        return newListOfHashMaps;
    }
}
