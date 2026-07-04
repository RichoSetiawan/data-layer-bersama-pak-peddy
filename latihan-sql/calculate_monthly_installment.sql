CREATE OR REPLACE FUNCTION calculate_monthly_installment(
    p_loan_amount NUMERIC,
    p_tenor_months INTEGER
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
declare
monthly_installment NUMERIC(18,2);
BEGIN
    IF p_tenor_months <= 0 THEN
        RAISE EXCEPTION 'Tenor bulan harus lebih besar dari 0';
    END IF;
    monthly_installment = round((p_loan_amount / p_tenor_months), 2);
    RETURN monthly_installment;
END $$;

select calculate_monthly_installment(10000000, 12) as monthly_installment;