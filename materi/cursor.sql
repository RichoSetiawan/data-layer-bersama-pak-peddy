call fifapp_credit_new.cursor_calculate_missing_loan_amounts();

CREATE OR REPLACE PROCEDURE fifapp_credit_new.cursor_calculate_missing_loan_amounts()
LANGUAGE plpgsql
AS $$
DECLARE
    cur_credit_applications CURSOR FOR
        SELECT
            id,
            vehicle_price,
            dp_amount
        FROM fifapp_credit_new.credit_applications
        WHERE approved_amount IS NULL
          AND vehicle_price IS NOT NULL
          AND dp_amount IS NOT NULL
        ORDER BY id;

    v_application_id BIGINT;
    v_vehicle_price NUMERIC;
    v_dp_amount NUMERIC;
    v_approved_amount NUMERIC(18, 2);
BEGIN
    OPEN cur_credit_applications;

    LOOP
        FETCH cur_credit_applications
        INTO
            v_application_id,
            v_vehicle_price,
            v_dp_amount;

        EXIT WHEN NOT FOUND;

        v_approved_amount := fifapp_credit_new.calculate_loan_amount(
            v_vehicle_price,
            v_dp_amount
        );

        UPDATE fifapp_credit_new.credit_applications
        SET
            approved_amount = v_approved_amount,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = v_application_id;

        RAISE NOTICE 'Application ID % updated with loan_amount %',
            v_application_id,
            v_approved_amount;
    END LOOP;

    CLOSE cur_credit_applications;
END;
$$;