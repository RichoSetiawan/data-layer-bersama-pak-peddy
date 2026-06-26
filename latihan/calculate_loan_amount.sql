CREATE OR REPLACE FUNCTION calculate_loan_amount(
    p_vehicle_price NUMERIC,
    p_dp_amount NUMERIC
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
declare 
loan_amount NUMERIC(18,2);
BEGIN
    IF p_vehicle_price < 0 OR p_dp_amount < 0 THEN
        RAISE EXCEPTION 'Harga kendaraan dan DP tidak boleh bernilai negatif';
    END IF;
    loan_amount := round((p_vehicle_price - p_dp_amount), 2);
    return loan_amount;
    -- RETURN round((p_vehicle_price - p_dp_amount), 2);
    -- RETURN (p_vehicle_price - p_dp_amount)::NUMERIC(18,2);
END $$;

select calculate_loan_amount(10000000, 100000) as loan_amount;