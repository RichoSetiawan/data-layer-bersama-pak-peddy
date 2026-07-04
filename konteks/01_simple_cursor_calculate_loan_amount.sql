/*
    Simple Cursor App 01
    Purpose:
    - Read credit applications that do not have loan_amount yet.
    - Calculate loan_amount using the existing helper function.
    - Update each application one by one through a simple cursor loop.

    Prerequisite:
    - Schema and table fifapp_credit.credit_applications already exist.
    - Function fifapp_credit.calculate_loan_amount(NUMERIC, NUMERIC) already exists.

    Run:
    CALL fifapp_credit.cursor_calculate_missing_loan_amounts();
*/

CREATE OR REPLACE PROCEDURE fifapp_credit.cursor_calculate_missing_loan_amounts()
LANGUAGE plpgsql
AS $$
DECLARE
    cur_credit_applications CURSOR FOR
        SELECT
            id,
            vehicle_price,
            dp_amount
        FROM fifapp_credit.credit_applications
        WHERE loan_amount IS NULL
          AND vehicle_price IS NOT NULL
          AND dp_amount IS NOT NULL
        ORDER BY id;

    v_application_id BIGINT;
    v_vehicle_price NUMERIC;
    v_dp_amount NUMERIC;
    v_loan_amount NUMERIC(18, 2);
BEGIN
    OPEN cur_credit_applications;

    LOOP
        FETCH cur_credit_applications
        INTO
            v_application_id,
            v_vehicle_price,
            v_dp_amount;

        EXIT WHEN NOT FOUND;

        v_loan_amount := fifapp_credit.calculate_loan_amount(
            v_vehicle_price,
            v_dp_amount
        );

        UPDATE fifapp_credit.credit_applications
        SET
            loan_amount = v_loan_amount,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = v_application_id;

        RAISE NOTICE 'Application ID % updated with loan_amount %',
            v_application_id,
            v_loan_amount;
    END LOOP;

    CLOSE cur_credit_applications;
END;
$$;
