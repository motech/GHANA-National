package org.motechproject.ghana.national.web.helper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.web.CWCController;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.BCG;
import static org.motechproject.ghana.national.domain.Concept.IMMUNIZATIONS_ORDERED;
import static org.motechproject.ghana.national.domain.Concept.IPTI;
import static org.motechproject.ghana.national.domain.Concept.MEASLES_1;
import static org.motechproject.ghana.national.domain.Concept.MEASLES_2;
import static org.motechproject.ghana.national.domain.Concept.OPV;
import static org.motechproject.ghana.national.domain.Concept.PENTA;
import static org.motechproject.ghana.national.domain.Concept.PNEUMOCOCCAL;
import static org.motechproject.ghana.national.domain.Concept.ROTAVIRUS;
import static org.motechproject.ghana.national.domain.Concept.SERIAL_NUMBER;
import static org.motechproject.ghana.national.domain.Concept.VITAMIN_A_BLUE;
import static org.motechproject.ghana.national.domain.Concept.VITAMIN_A_RED;
import static org.motechproject.ghana.national.domain.Concept.YF;

@Component
public class CwcFormMapper {
    public CWCEnrollmentForm mapEncounterToView(MRSEncounter encounter) {
        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();
        final FacilityForm facilityForm = new FacilityForm();
        final MRSFacility facility = encounter.getFacility();
        facilityForm.setCountry(facility.getCountry());
        facilityForm.setRegion(facility.getRegion());
        facilityForm.setCountyDistrict(facility.getCountyDistrict());
        facilityForm.setStateProvince(facility.getStateProvince());
        facilityForm.setFacilityId(facility.getId());
        facilityForm.setName(facility.getName());
        cwcEnrollmentForm.setFacilityForm(facilityForm);
        cwcEnrollmentForm.setRegistrationDate(encounter.getDate());
        cwcEnrollmentForm.setPatientMotechId(encounter.getPatient().getMotechId());
        cwcEnrollmentForm.setStaffId(encounter.getCreator().getSystemId());

        Set<MRSObservation> observations = encounter.getObservations();
        final ArrayList<CwcCareHistory> careHistories = new ArrayList<CwcCareHistory>();
        for (MRSObservation observation : observations) {

            if (IMMUNIZATIONS_ORDERED.getName().equals(observation.getConceptName())) {
                final String conceptName = ((MRSConcept) observation.getValue()).getName();
                if (YF.getName().equals(conceptName)) {
                    cwcEnrollmentForm.setYfDate(observation.getDate());
                    careHistories.add(CwcCareHistory.YF);
                }
                if (BCG.getName().equals(conceptName)) {
                    cwcEnrollmentForm.setBcgDate(observation.getDate());
                    careHistories.add(CwcCareHistory.BCG);
                }
                if (VITAMIN_A_BLUE.getName().equals(conceptName) || VITAMIN_A_RED.getName().equals(conceptName)) {
                    cwcEnrollmentForm.setVitADate(observation.getDate());
                    cwcEnrollmentForm.setLastVitaminA(VITAMIN_A_BLUE.getName().equals(conceptName) ?
                            Constants.VITAMIN_A_BLUE : Constants.VITAMIN_A_RED);
                    careHistories.add(CwcCareHistory.VITA_A);
                }
                if (MEASLES_1.getName().equals(conceptName) || MEASLES_2.getName().equals(conceptName)) {
                    cwcEnrollmentForm.setMeaslesDate(observation.getDate());
                    cwcEnrollmentForm.setLastMeasles(MEASLES_1.getName().equals(conceptName) ? 1 : 2);
                    careHistories.add(CwcCareHistory.MEASLES);
                }
            }

            if (IPTI.getName().equals(observation.getConceptName())) {
                cwcEnrollmentForm.setLastIPTiDate(observation.getDate());
                cwcEnrollmentForm.setLastIPTi(((Double) observation.getValue()).intValue());
                careHistories.add(CwcCareHistory.IPTI);
            }

            if (PENTA.getName().equals(observation.getConceptName())) {
                cwcEnrollmentForm.setLastPentaDate(observation.getDate());
                cwcEnrollmentForm.setLastPenta(((Double) observation.getValue()).intValue());
                careHistories.add(CwcCareHistory.PENTA);
            }
            if (ROTAVIRUS.getName().equals(observation.getConceptName())) {
                cwcEnrollmentForm.setLastRotavirusDate(observation.getDate());
                cwcEnrollmentForm.setLastRotavirus(((Double) observation.getValue()).intValue());
                careHistories.add(CwcCareHistory.ROTAVIRUS);
            }

            if (PNEUMOCOCCAL.getName().equals(observation.getConceptName())) {
                cwcEnrollmentForm.setLastPneumococcalDate(observation.getDate());
                cwcEnrollmentForm.setLastPneumococcal(((Double) observation.getValue()).intValue());
                careHistories.add(CwcCareHistory.PNEUMOCOCCAL);
            }
            if (OPV.getName().equals(observation.getConceptName())) {
                cwcEnrollmentForm.setLastOPVDate(observation.getDate());
                cwcEnrollmentForm.setLastOPV(((Double) observation.getValue()).intValue());
                careHistories.add(CwcCareHistory.OPV);
            }

            cwcEnrollmentForm.setCareHistory(careHistories);
            if (SERIAL_NUMBER.getName().equals(observation.getConceptName())) {
                cwcEnrollmentForm.setSerialNumber((String) observation.getValue());
            }
        }
        cwcEnrollmentForm.setAddHistory(!careHistories.isEmpty());
        return cwcEnrollmentForm;
    }

    public Map<String, Object> setViewAttributes() {
        Map<String, Object> map = new HashMap<String, Object>();

        List<CwcCareHistory> careHistories = Arrays.asList(CwcCareHistory.BCG, CwcCareHistory.IPTI,
                CwcCareHistory.MEASLES, CwcCareHistory.OPV, CwcCareHistory.PENTA, CwcCareHistory.VITA_A,
                CwcCareHistory.YF, CwcCareHistory.ROTAVIRUS, CwcCareHistory.PNEUMOCOCCAL);

        Map<CwcCareHistory, String> cwcCareHistories = new LinkedHashMap<CwcCareHistory, String>();
        for (CwcCareHistory cwcCareHistory : careHistories) {
            cwcCareHistories.put(cwcCareHistory, cwcCareHistory.getDescription());
        }
        map.put(Constants.CARE_HISTORIES, cwcCareHistories);

        Map<RegistrationToday, String> registrationTodayValues = new LinkedHashMap<RegistrationToday, String>();
        for (RegistrationToday registrationToday : Arrays.asList(RegistrationToday.TODAY, RegistrationToday.IN_PAST,
                RegistrationToday.IN_PAST_IN_OTHER_FACILITY)) {
            registrationTodayValues.put(registrationToday, registrationToday.getDescription());
        }

        map.put(CWCController.REGISTRATION_OPTIONS, registrationTodayValues);
        map.put(Constants.LAST_IPTI, new LinkedHashMap<Integer, String>() {{
            put(1, Constants.IPTI_1);
            put(2, Constants.IPTI_2);
            put(3, Constants.IPTI_3);
        }});
        map.put(Constants.LAST_OPV, new LinkedHashMap<Integer, String>() {{
            put(0, Constants.OPV_0);
            put(1, Constants.OPV_1);
            put(2, Constants.OPV_2);
            put(3, Constants.OPV_3);
        }});
        map.put(Constants.LAST_PENTA, new LinkedHashMap<Integer, String>() {{
            put(1, Constants.PENTA_1);
            put(2, Constants.PENTA_2);
            put(3, Constants.PENTA_3);
        }});
        map.put(Constants.LAST_ROTAVIRUS, new LinkedHashMap<Integer, String>() {{
            put(1, Constants.ROTAVIRUS_1);
            put(2, Constants.ROTAVIRUS_2);
        }});
        map.put(Constants.LAST_VITA, new LinkedHashMap<String, String>() {{
            put(Constants.VITAMIN_A_BLUE, StringUtils.capitalize(Constants.VITAMIN_A_BLUE));
            put(Constants.VITAMIN_A_RED, StringUtils.capitalize(Constants.VITAMIN_A_RED));
        }});
        map.put(Constants.LAST_MEASLES, new LinkedHashMap<Integer, String>() {{
            put(1, Constants.MEASLES_1);
            put(2, Constants.MEASLES_2);
        }});
        map.put(Constants.LAST_PNEUMOCOCCAL, new LinkedHashMap<Integer, String>() {{
            put(1, Constants.PNEUMOCOCCAL_1);
            put(2, Constants.PNEUMOCOCCAL_2);
            put(3, Constants.PNEUMOCOCCAL_3);
        }});
        return map;
    }
}
