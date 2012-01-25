package org.motechproject.ghana.national.tools.seed.data;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Map<String, Object>> newListOfHashMaps = new ArrayList<Map<String, Object>>();
        for (HashMap<String, String> map : enrollmentData) {
            String patientId = map.get("patientId");
            final HashMap<String, Object> newHashMap = new HashMap<String, Object>();

            Patient patient = allPatients.patientByOpenmrsId(patientId);
            if (patient == null) continue;  //patient has no motech id
            if (patient.getMrsPatient().getMotechId() == null) {  //patient is voided. can't get details from api, hence direct sql call.
                patient = new Patient(new MRSPatient(mobileMidwifeSource.getPatientMotechId(patientId), null, patient.getMrsPatient().getFacility()));
            }
            newHashMap.put("patient", patient);
            newHashMap.put("programName", map.get("programName"));
            newHashMap.put("startDate", map.get("startDate"));
            newListOfHashMaps.add(newHashMap);
        }


        for (MobileMidwifeEnrollment mobileMidwifeEnrollment : mobileMidwifeEnrollments) {
            String patientId = mobileMidwifeEnrollment.getPatientId();

            final List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map : newListOfHashMaps) {
                final Patient patient = (Patient) map.get("patient");
                if (patient != null && patient.getMrsPatient().getMotechId().equals(patientId)) {
                    newList.add(map);
                }
            }

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

            for (int i = 0; i < newList.size(); i++) {
                mobileMidwifeEnrollment = MobileMidwifeEnrollment.cloneNew(mobileMidwifeEnrollment);
                mobileMidwifeEnrollment.setServiceType((((String) newList.get(i).get("programName")).contains("Pregnancy")) ? ServiceType.PREGNANCY : ServiceType.CHILD_CARE);
                mobileMidwifeEnrollment.setMessageStartWeek("1");
                mobileMidwifeEnrollment.setActive(i == 0);
                mobileMidwifeEnrollment.setStaffId("admin");
                mobileMidwifeEnrollment.setConsent(true);

                Patient patient = (Patient) newList.get(i).get("patient");
                mobileMidwifeEnrollment.setPatientId(patient.getMrsPatient().getMotechId());

                String facilityId;
                if (patient.getMrsPatient().getFacility() == null) {
                    facilityId = "1"; //unknown location
                } else {
                    facilityId = patient.getMrsPatient().getFacility().getId();
                }

                String facilityMotechId;
                if (FACILITY_CACHE.get(facilityId) != null) {
                    facilityMotechId = FACILITY_CACHE.get(facilityId);
                } else {
                    facilityMotechId = allFacilities.findByMrsFacilityId(facilityId).getMotechId();
                    FACILITY_CACHE.put(facilityId, facilityMotechId);
                }
                mobileMidwifeEnrollment.setFacilityId(facilityMotechId);

                allMobileMidwifeEnrollments.add(mobileMidwifeEnrollment);
            }

        }
    }
}
