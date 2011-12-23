update patient_identifier pi,motechmodule_community_patient comp, motechmodule_community comf, motechmodule_facility fac
set pi.location_id = fac.location_id
where
comf.id=comp.community_id and
comf.facility_id=fac.id and
comp.patient_id = pi.patient_id and
pi.patient_id NOT IN ( select motPatient.patient_id from motechmodule_facility_patient motPatient);