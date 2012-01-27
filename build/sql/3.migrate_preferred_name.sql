/** Get all records which has more than one person_name for a person_id */
-- select * from person_name where person_id in (select person_id from person_name group by person_id having (count(person_id) > 1));
/** Get all records which has more than one person_name for a person_id and not preferred */
-- select * from person_name where person_id in (select person_id from person_name where person_name_id group by person_id having (count(person_id) > 1)) and preferred = 1 and person_name_id not in (select person_name_id from person_name where preferred =1 and given_name != 'Super');
update person_name as target left join person_name as src on src.preferred = 0 set src.given_name = target.given_name where target.person_id = src.person_id;
-- delete from person_name where person_name_id in (select src.person_name_id from person_name as src join person_name as target where src.person_id = target.person_id and src.preferred = target.preferred and src.person_name_id != target.person_name_id and src.voided = target.voided and src.date_created < target.date_created);
delete src from person_name as src join person_name as target where src.person_id = target.person_id and src.preferred = target.preferred and src.person_name_id != target.person_name_id and src.voided = target.voided and src.date_created < target.date_created;
delete from person_name where person_id in (select person_id from (select person_id from person_name where preferred = 0) as temp_deletion) and preferred = 1;

