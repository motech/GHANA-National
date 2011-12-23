update patient_identifier pi,motechmodule_facility fac, motechmodule_facility_patient facPatient
set pi.location_id = fac.location_id
where fac.id=facPatient.facility_id and  pi.patient_id = facPatient.patient_id;
