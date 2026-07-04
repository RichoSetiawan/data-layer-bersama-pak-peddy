CREATE OR REPLACE PROCEDURE finalize_credit_application(
    p_application_id BIGINT,
    p_processed_by_user_id BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_lama VARCHAR(30);
    v_loan_amount_lama NUMERIC(18,2);
    v_vehicle_price NUMERIC(18,2);
    v_dp_amount NUMERIC(18,2);
    v_tenor_months INT;
    v_birth_date DATE;
    v_monthly_income NUMERIC(18,2);
    
    v_customer_age INT;
    v_loan_amount_baru NUMERIC(18,2);
    v_monthly_installment NUMERIC(18,2);
    v_dbr_percentage NUMERIC(5,2);
    
    v_income_score INT;
    v_age_score INT;
    v_loan_amount_score INT;
    v_dbr_score INT;
    v_risk_score_akhir INT;
    v_risk_level VARCHAR(30);
    
    v_status_baru VARCHAR(30);
    v_decision_reason TEXT;
    v_approved_amount NUMERIC(18,2) := 0;
    v_risk_id BIGINT;
    
    v_action_audit VARCHAR(50);
    v_old_value_json TEXT;
    v_new_value_json TEXT;
BEGIN
    if p_application_id is null then 
    raise EXCEPTION 'Application ID cannot be null' using ERRCODE = '22004';
    end if;

    if p_processed_by_user_id is null then
    raise EXCEPTION 'Processed by user ID cannot be null' using ERRCODE = '22004';
    end if;

    IF NOT EXISTS (
    SELECT 1 
    FROM fifapp_credit_new.users
    WHERE id = p_processed_by_user_id
	) THEN 
	    RAISE EXCEPTION 'Processed by user not found: %', p_processed_by_user_id 
	    USING ERRCODE = 'P0002';
	END IF;

    
    SELECT
        ca.status, ca.loan_amount, ca.vehicle_price, ca.dp_amount, ca.tenor_months, 
        c.birth_date, c.monthly_income
    INTO
        v_status_lama, v_loan_amount_lama, v_vehicle_price, v_dp_amount, v_tenor_months, 
        v_birth_date, v_monthly_income
    FROM fifapp_credit_new.credit_applications ca
    JOIN fifapp_credit_new.customers c ON ca.customer_id = c.id
    WHERE ca.id = p_application_id
    FOR UPDATE OF ca;

	IF v_status_lama IS NULL THEN
        RAISE EXCEPTION 'Aplikasi kredit dengan ID % tidak ditemukan.', p_application_id;
    ELSIF v_status_lama IN ('APPROVED', 'REJECTED', 'CANCELLED') THEN
        RAISE EXCEPTION 'Aplikasi ID % tidak dapat diproses karena sudah berstatus akhir: %', 
                        p_application_id, v_status_lama;
    END IF;


    v_customer_age := EXTRACT(YEAR FROM AGE(CURRENT_DATE, v_birth_date));
    v_loan_amount_baru := calculate_loan_amount(v_vehicle_price, v_dp_amount);
    v_monthly_installment := calculate_monthly_installment(v_loan_amount_baru, v_tenor_months);
    v_dbr_percentage := calculate_dbr(v_monthly_installment, v_monthly_income);

    v_status_baru := determine_credit_status(v_dbr_percentage, v_customer_age, v_monthly_income);

    v_income_score := LEAST(100, GREATEST(0, ((v_monthly_income / 3000000) * 50)::INT));
    IF v_customer_age BETWEEN 25 AND 50 THEN v_age_score := 100; ELSE v_age_score := 60; END IF;
    v_loan_amount_score := (100 - ((v_loan_amount_baru / v_vehicle_price) * 100))::INT;
    v_dbr_score := (100 - (v_dbr_percentage * 2))::INT;

    IF v_status_baru = 'REJECTED' THEN
        v_risk_score_akhir := 0;
    ELSE
        v_risk_score_akhir := ((v_income_score + v_age_score + v_loan_amount_score + v_dbr_score) / 4);
    END IF;

    IF v_risk_score_akhir >= 70 THEN
        v_risk_level := 'LOW';
    ELSIF v_risk_score_akhir >= 45 THEN
        v_risk_level := 'MEDIUM';
    ELSE
        v_risk_level := 'HIGH';
    END IF;

    IF v_customer_age < 21 THEN
        v_decision_reason := format('REJECTED: Usia pemohon (%s tahun) di bawah batas minimal 21 tahun.', v_customer_age);
    ELSIF v_monthly_income < 3000000 THEN
        v_decision_reason := format('REJECTED: Gaji bulanan (Rp %s) di bawah standar kelayakan.', v_monthly_income);
    ELSIF v_dbr_percentage > 50 THEN
        v_decision_reason := format('REJECTED: Beban utang terlalu ekstrem (DBR: %s%%), melebihi batas aman 50%%.', v_dbr_percentage);
    ELSIF v_status_baru = 'MANUAL_REVIEW' THEN
        v_decision_reason := format('MANUAL_REVIEW: DBR (%s%%) berada di zona menengah (36%% - 50%%).', v_dbr_percentage);
    ELSE
        v_decision_reason := format('APPROVED: Parameter keuangan sehat. DBR %s%% dengan tingkat risiko %s.', v_dbr_percentage, v_risk_level);
        v_approved_amount := v_loan_amount_baru;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM fifapp_credit_new.risk_assessments
        WHERE credit_application_id = p_application_id
    ) THEN
        RAISE EXCEPTION 'Risk assessment already exists for application ID: %', p_application_id
            USING ERRCODE = '23505';
    END IF;

    INSERT INTO fifapp_credit_new.risk_assessments (
        credit_application_id, assessed_by, risk_score, risk_level, decision,
        income_score, age_score, loan_amount_score, dbr_score, previous_payment_score,
        dbr_percentage, notes, assessed_at, created_at, updated_at
    ) VALUES (
        p_application_id, p_processed_by_user_id, v_risk_score_akhir, v_risk_level, v_status_baru,
        v_income_score, v_age_score, v_loan_amount_score, v_dbr_score, 100,
        v_dbr_percentage, v_decision_reason, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_risk_id;

    UPDATE fifapp_credit_new.credit_applications
    SET
        loan_amount = v_loan_amount_baru,
        approved_amount = CASE WHEN v_status_baru = 'APPROVED' THEN v_approved_amount ELSE 0.00 END,
        status = v_status_baru,
        decision_reason = v_decision_reason,
        updated_at = CURRENT_TIMESTAMP,
        approved_at = CASE WHEN v_status_baru = 'APPROVED' THEN CURRENT_TIMESTAMP ELSE NULL END,
        rejected_at = CASE WHEN v_status_baru = 'REJECTED' THEN CURRENT_TIMESTAMP ELSE NULL END,
        manual_review_at = CASE WHEN v_status_baru = 'MANUAL_REVIEW' THEN CURRENT_TIMESTAMP ELSE NULL END,
        cancelled_at = NULL
    WHERE id = p_application_id;

    IF v_status_baru = 'APPROVED' THEN
        CALL generate_installments(p_application_id);
    END IF;

    IF v_status_baru = 'APPROVED' THEN
        v_action_audit := 'APPROVE';
    ELSIF v_status_baru = 'REJECTED' THEN
        v_action_audit := 'REJECT';
    ELSE
        v_action_audit := 'UPDATE';
    END IF;

    v_old_value_json := format('{"status": "%s", "loan_amount": %s}', v_status_lama, COALESCE(v_loan_amount_lama, 0.00));
    v_new_value_json := format(
        '{"status": "%s", "loan_amount": %s, "monthly_installment": %s, "dbr_percentage": %s, "risk_assessment_id": %s}',
        v_status_baru, v_loan_amount_baru, v_monthly_installment, v_dbr_percentage, v_risk_id
    );

    INSERT INTO fifapp_credit_new.audit_logs (
        user_id,
        entity_name,
        entity_id,
        action,
        old_value,
        new_value,
        ip_address,
        user_agent,
        created_at
    ) VALUES (
        p_processed_by_user_id,
        'credit_applications',
        p_application_id,
        v_action_audit,
        v_old_value_json,
        v_new_value_json,
        '127.0.0.1',
        'PostgreSQL Engine PL/pgSQL Internal',
        CURRENT_TIMESTAMP
    );

    RAISE NOTICE 'Prosedur Finalisasi Sukses dicatat pada Audit Log Baru dengan aksi %.', v_action_audit;
END $$;

UPDATE fifapp_credit_new.credit_applications SET status = 'SUBMITTED' WHERE id = 3;

CALL finalize_credit_application(355, 1000);

SELECT id, user_id, action, old_value, new_value, ip_address, created_at 
FROM fifapp_credit_new.audit_logs 
WHERE entity_id = 3
ORDER BY id DESC LIMIT 1;