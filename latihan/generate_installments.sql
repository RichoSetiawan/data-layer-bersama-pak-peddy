CREATE OR REPLACE PROCEDURE generate_installments(
    p_application_id BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_application_date DATE;
    v_loan_amount NUMERIC(18,2);
    v_tenor_months INT;
    v_status VARCHAR(30);
    
    v_monthly_installment NUMERIC(18,2);
    v_current_due_date DATE;
    i INT;
    v_existing_count INT;
BEGIN
    SELECT application_date, loan_amount, tenor_months, status
    INTO v_application_date, v_loan_amount, v_tenor_months, v_status
    FROM fifapp_credit_new.credit_applications
    WHERE id = p_application_id;

    -- Validasi keberadaan data aplikasi
    -- IF v_status IS NULL THEN
    --     RAISE EXCEPTION 'Pengajuan kredit dengan ID % tidak ditemukan.', p_application_id;
    -- END IF;
    -- Validasi keberadaan versi pak peddy
    IF NOT FOUND THEN 
    RAISE EXCEPTION 'Credit application not found: %', p_application_id using ERRCODE = 'P0002';
    END IF;

    IF v_status != 'APPROVED' THEN
        RAISE EXCEPTION 'Gagal membuat cicilan: Aplikasi ID % masih berstatus %, wajib berstatus APPROVED.', 
                        p_application_id, v_status using ERRCODE = '23514';
    END IF;

    SELECT COUNT(*) INTO v_existing_count
    FROM fifapp_credit_new.installments
    WHERE credit_application_id = p_application_id;

    IF v_existing_count > 0 THEN
        RAISE EXCEPTION 'Gagal membuat cicilan: Jadwal cicilan untuk Aplikasi ID % sudah pernah digenerate.', 
                        p_application_id using ERRCODE = 'P0002';
    END IF;

    v_monthly_installment := calculate_monthly_installment(v_loan_amount, v_tenor_months);
    LOCK TABLE installments in exclusive MODE;
    FOR i IN 1..v_tenor_months LOOP
        
        v_current_due_date := (v_application_date + (i || ' months')::interval)::date;

        INSERT INTO fifapp_credit_new.installments (
            credit_application_id,
            installment_number,
            due_date,
            amount,
            paid_amount,
            outstanding_amount,
            payment_date,
            days_overdue,
            status,
            created_at,
            updated_at
        ) VALUES (
            p_application_id,                  -- credit_application_id
            i,                                 -- installment_number (ke-i)
            v_current_due_date,                -- due_date
            v_monthly_installment,             -- amount
            0.00,                              -- paid_amount (Awal: Rp 0)
            v_monthly_installment,             -- outstanding_amount (Awal: sama dengan nominal cicilan)
            NULL,                              -- payment_date (Belum bayar)
            0,                                 -- days_overdue
            'UNPAID',                          -- status (8. Status awal adalah UNPAID)
            CURRENT_TIMESTAMP,                 -- created_at
            CURRENT_TIMESTAMP                  -- updated_at
        );
        
    END LOOP;

    RAISE NOTICE 'Prosedur Sukses: Berhasil membuat % jadwal cicilan UNPAID untuk Aplikasi ID %.', 
            v_tenor_months, p_application_id;
END $$;

CALL generate_installments(3); 


UPDATE fifapp_credit_new.credit_applications 
SET status = 'APPROVED', loan_amount = 12000000.00, tenor_months = 12, application_date = '2026-06-01' 
WHERE id = 3;

CALL generate_installments(3);


SELECT credit_application_id, installment_number, due_date, amount, outstanding_amount, status 
FROM fifapp_credit_new.installments 
WHERE credit_application_id = 3
ORDER BY installment_number ASC;


CALL generate_installments(3);
