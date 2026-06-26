create or replace function determine_credit_status(
    p_dbr_percentage NUMERIC,
    p_customer_age INT,
    p_monthly_salary NUMERIC
)
RETURNS varchar
LANGUAGE plpgsql
as $$
BEGIN
    CASE 
    WHEN p_customer_age < 21 THEN return 'REJECTED';
    WHEN p_monthly_salary < 3000000 THEN return 'REJECTED';
    WHEN p_dbr_percentage <= 35 then return 'APPROVED';
    WHEN p_dbr_percentage > 35 AND p_dbr_percentage <= 50 THEN return 'MANUAL_REVIEW';
    ELSE RETURN 'REJECTED';
    END CASE;
END $$;

select determine_credit_status(35, 20, 10000000);