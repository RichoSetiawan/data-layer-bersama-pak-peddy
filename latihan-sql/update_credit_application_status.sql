CREATE OR REPLACE PROCEDURE update_credit_application_status(
    p_application_id BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_awal VARCHAR(30);
    v_loan_amount NUMERIC(18,2);
    v_tenor_months INT;
    v_birth_date DATE;
    v_monthly_income NUMERIC(18,2);
    
    v_customer_age INT;
    v_monthly_installment NUMERIC(18,2);
    v_dbr_percentage NUMERIC(5,2);
    
    v_status_baru VARCHAR(30);
    v_decision_reason TEXT;
BEGIN
    SELECT 
        ca.status, ca.loan_amount, ca.tenor_months, 
        c.birth_date, c.monthly_income
    INTO 
        v_status_awal, v_loan_amount, v_tenor_months, 
        v_birth_date, v_monthly_income
    FROM fifapp_credit_new.credit_applications ca
    JOIN fifapp_credit_new.customers c ON ca.customer_id = c.id
    WHERE ca.id = p_application_id;

    IF v_status_awal IS NULL THEN
        RAISE EXCEPTION 'Pengajuan kredit dengan ID % tidak ditemukan.', p_application_id;
    ELSIF v_status_awal = 'CANCELLED' THEN
        RAISE EXCEPTION 'Aplikasi tidak dapat diproses ulang karena sudah dibatalkan (CANCELLED).';
    END IF;

    v_customer_age := EXTRACT(YEAR FROM AGE(CURRENT_DATE, v_birth_date));

    v_monthly_installment := calculate_monthly_installment(v_loan_amount, v_tenor_months);

    v_dbr_percentage := calculate_dbr(v_monthly_installment, v_monthly_income);

    v_status_baru := determine_credit_status(v_dbr_percentage, v_customer_age, v_monthly_income);

    IF v_customer_age < 21 THEN
        v_decision_reason := format('Ditolak otomatis karena usia pemohon (%s tahun) di bawah batas minimal 21 tahun.', v_customer_age);
    ELSIF v_monthly_income < 3000000 THEN
        v_decision_reason := format('Ditolak otomatis karena pendapatan bulanan (Rp %s) di bawah standar Rp 3.000.000.', v_monthly_income);
    ELSIF v_dbr_percentage <= 35 THEN
        v_decision_reason := format('Disetujui otomatis oleh sistem karena rasio kemampuan bayar (DBR: %s%%) sangat sehat.', v_dbr_percentage);
    ELSIF v_dbr_percentage > 35 AND v_dbr_percentage <= 50 THEN
        v_decision_reason := format('Dibutuhkan tinjauan manual oleh komite kredit karena rasio utang (DBR: %s%%) berada di zona menengah.', v_dbr_percentage);
    ELSE
        v_decision_reason := format('Ditolak otomatis karena rasio pengeluaran cicilan terlalu ekstrem (DBR: %s%%), melebihi batas aman 50%%.', v_dbr_percentage);
    END IF;

    UPDATE fifapp_credit_new.credit_applications
    SET 
        status = v_status_baru,
        decision_reason = v_decision_reason,
        updated_at = CURRENT_TIMESTAMP,
        
        approved_at = CASE WHEN v_status_baru = 'APPROVED' THEN CURRENT_TIMESTAMP ELSE NULL END,
        rejected_at = CASE WHEN v_status_baru = 'REJECTED' THEN CURRENT_TIMESTAMP ELSE NULL END,
        manual_review_at = CASE WHEN v_status_baru = 'MANUAL_REVIEW' THEN CURRENT_TIMESTAMP ELSE NULL END,
        cancelled_at = NULL
    WHERE id = p_application_id;

    RAISE NOTICE 'Prosedur Sukses: ID Aplikasi % telah diperbarui ke status %.', p_application_id, v_status_baru;
END $$;

call process_submit_application(3);
call update_credit_application_status(3);



