CREATE OR REPLACE FUNCTION fifapp_credit.calculate_loan_amount_after()
 RETURNS TRIGGER
 LANGUAGE plpgsql
AS $$
declare
    calculated_loan NUMERIC;
BEGIN
    IF NEW.vehicle_price < 0 OR NEW.dp_amount < 0 THEN
        RAISE EXCEPTION 'Harga kendaraan dan DP tidak boleh bernilai negatif';
    END IF;

    calculated_loan := round((NEW.vehicle_price - NEW.dp_amount), 2);

    IF COALESCE(NEW.loan_amount, -1) <> calculated_loan THEN
        UPDATE credit_applications
        SET loan_amount = calculated_loan
        WHERE id = NEW.id; 
    END IF;

    RETURN NULL; 
END $$;

CREATE OR REPLACE TRIGGER trigger_kalkulasi
AFTER INSERT OR UPDATE ON credit_applications
FOR EACH ROW
EXECUTE FUNCTION fifapp_credit.calculate_loan_amount_after();

