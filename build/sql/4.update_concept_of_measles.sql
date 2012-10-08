update concept
set datatype_id = (select cd.concept_datatype_id from concept_datatype cd where cd.name = 'Numeric')
where concept_id = (select cn.concept_id from concept_name cn where cn.name = 'MEASLES VACCINATION');