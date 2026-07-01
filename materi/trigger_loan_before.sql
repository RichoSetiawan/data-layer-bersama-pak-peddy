CREATE OR REPLACE FUNCTION fifapp_credit.calculate_loan_amount_before()
 RETURNS TRIGGER
 LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.vehicle_price < 0 OR NEW.dp_amount < 0 THEN
        RAISE EXCEPTION 'Harga kendaraan dan DP tidak boleh bernilai negatif';
    END IF;

    NEW.loan_amount := round((NEW.vehicle_price - NEW.dp_amount), 2);

    RETURN NEW;
END $$;

CREATE OR REPLACE TRIGGER trigger_calculate_loan_amount_before
BEFORE INSERT OR UPDATE ON credit_applications
FOR EACH ROW
EXECUTE FUNCTION fifapp_credit.calculate_loan_amount_before();
