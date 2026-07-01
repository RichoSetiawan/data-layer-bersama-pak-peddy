create or replace function fifapp_credit_new.count_credit_applications_by_status(
p_status VARCHAR, 
p_schema VARCHAR,
p_table VARCHAR
)
returns  BIGINT
language plpgsql
as $$
declare
v_sql TEXT;
v_total_applications BIGINT;
begin 
	if p_status is null then
	raise exception 'Status cannot be null' using ERRCODE = '22004';
	end if;

--v_sql := FORMAT('SELECT COUNT(*) FROM %I.%I 
--WHERE status = $1', 
--'fifapp_credit_new', 
--'credit_applications');
v_sql := FORMAT('SELECT COUNT(*) FROM %I.%I 
WHERE status = $1', 
p_schema, 
p_table);
execute v_sql
into v_total_applications
using p_status;
return v_total_applications;
end;
$$;

comment on function fifapp_credit_new.count_credit_applications_by_status(VARCHAR) is 'Simple dynamic SQL demo that counts credit applications by status';
select fifapp_credit_new.count_credit_applications_by_status('APPROVED') as jumlah_aplikasi_berdasarkan_status;
select fifapp_credit_new.count_credit_applications_by_status('APPROVED', 'fifapp_credit_new', 'credit_applications') as jumlah_aplikasi_berdasarkan_status;