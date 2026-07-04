CREATE OR REPLACE FUNCTION calculate_dbr(
    p_monthly_installment NUMERIC,
    p_monthly_income NUMERIC
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
declare
dbr_percentage NUMERIC(18,2);
BEGIN
    IF p_monthly_income <= 0 THEN
        RAISE EXCEPTION 'Pendapatan bulanan harus lebih besar dari 0';
    END IF;
    dbr_percentage = round(((p_monthly_installment / p_monthly_income) * 100),2);
    RETURN dbr_percentage;
END $$;

select calculate_dbr(1000000, 5000000) as dbr_percentage;
