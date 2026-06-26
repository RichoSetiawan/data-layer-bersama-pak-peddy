-- =====================================================
-- FIF Credit Application - SMALL Synthetic Seed Data
-- PostgreSQL only
-- Target size:
--   branches: 100
--   users: 1,000
--   customers: 10,000
--   dealers: 500
--   vehicles: 5,000
--   credit_applications: 30,000
--   risk_assessments: ~29,700
--   installments: ~360,000
--   payments: ~220,000 - 260,000 depending status mix
--   audit_logs: ~120,000
--   OLAP dimensions + facts populated from OLTP
-- =====================================================

SET search_path TO fifapp_credit;
SET client_min_messages TO warning;

-- Optional but useful for repeatable labs.
-- Remove/comment this block if you want to append data instead of reset.
TRUNCATE TABLE
    audit_logs,
    payments,
    installments,
    risk_assessments,
    credit_applications,
    vehicles,
    dealers,
    customers,
    users,
    branches
RESTART IDENTITY CASCADE;

-- Make random generation repeatable enough for lab demos.
SELECT setseed(0.42);

-- Speed up bulk insert for lab environment.
-- Use LOCAL only inside transaction if preferred.
SET synchronous_commit = off;

-- =====================================================
-- 1) MASTER / REFERENCE DATA
-- =====================================================

INSERT INTO branches (branch_code, branch_name, region, city, address)
SELECT
    'BR' || LPAD(gs::text, 4, '0') AS branch_code,
    'FIF Branch ' || gs AS branch_name,
    (ARRAY['Jabodetabek', 'West Java', 'Central Java', 'East Java', 'Bali Nusra', 'Sumatra', 'Kalimantan', 'Sulawesi'])[1 + ((gs - 1) % 8)] AS region,
    (ARRAY['Jakarta', 'Bogor', 'Depok', 'Tangerang', 'Bekasi', 'Bandung', 'Semarang', 'Surabaya', 'Denpasar', 'Medan', 'Palembang', 'Makassar'])[1 + ((gs - 1) % 12)] AS city,
    'Jl. Training Data No. ' || gs AS address
FROM generate_series(1, 100) gs;

INSERT INTO users (branch_id, employee_number, username, full_name, role, is_active)
SELECT
    1 + ((gs - 1) % 100) AS branch_id,
    'EMP' || LPAD(gs::text, 6, '0') AS employee_number,
    'user' || LPAD(gs::text, 6, '0') AS username,
    'Employee ' || gs AS full_name,
    CASE
        WHEN gs % 100 < 60 THEN 'AGENT'
        WHEN gs % 100 < 80 THEN 'CREDIT_ANALYST'
        WHEN gs % 100 < 90 THEN 'COLLECTION'
        WHEN gs % 100 < 97 THEN 'SUPERVISOR'
        ELSE 'ADMIN'
    END AS role,
    CASE WHEN gs % 50 = 0 THEN FALSE ELSE TRUE END AS is_active
FROM generate_series(1, 1000) gs;

INSERT INTO customers (
    customer_number, full_name, nik, birth_date, gender, phone_number, email, address, city,
    occupation, monthly_income, income_range
)
SELECT
    'CUST' || LPAD(gs::text, 8, '0') AS customer_number,
    'Customer ' || gs AS full_name,
    '3275' || LPAD(gs::text, 12, '0') AS nik,
    DATE '1965-01-01' + ((random() * 15000)::int) AS birth_date,
    CASE WHEN random() < 0.52 THEN 'MALE' ELSE 'FEMALE' END AS gender,
    '08' || LPAD((1000000000 + gs)::text, 10, '0') AS phone_number,
    'customer' || gs || '@example.com' AS email,
    'Alamat Customer No. ' || gs AS address,
    (ARRAY['Jakarta', 'Bogor', 'Depok', 'Tangerang', 'Bekasi', 'Bandung', 'Semarang', 'Surabaya', 'Denpasar', 'Medan', 'Palembang', 'Makassar'])[1 + ((gs - 1) % 12)] AS city,
    (ARRAY['Employee', 'Entrepreneur', 'Teacher', 'Driver', 'Merchant', 'Freelancer', 'Civil Servant'])[1 + ((gs - 1) % 7)] AS occupation,
    CASE
        WHEN bucket < 30 THEN (2500000 + random() * 2500000)::numeric(18,2)
        WHEN bucket < 75 THEN (5000000 + random() * 7000000)::numeric(18,2)
        WHEN bucket < 95 THEN (12000000 + random() * 18000000)::numeric(18,2)
        ELSE (30000000 + random() * 70000000)::numeric(18,2)
    END AS monthly_income,
    CASE
        WHEN bucket < 30 THEN 'LOW'
        WHEN bucket < 75 THEN 'MEDIUM'
        WHEN bucket < 95 THEN 'HIGH'
        ELSE 'VERY_HIGH'
    END AS income_range
FROM (
    SELECT gs, (random() * 100)::int AS bucket
    FROM generate_series(1, 10000) gs
) s;

INSERT INTO dealers (dealer_code, dealer_name, city, region, is_active)
SELECT
    'DLR' || LPAD(gs::text, 5, '0') AS dealer_code,
    'Dealer Partner ' || gs AS dealer_name,
    (ARRAY['Jakarta', 'Bogor', 'Depok', 'Tangerang', 'Bekasi', 'Bandung', 'Semarang', 'Surabaya', 'Denpasar', 'Medan', 'Palembang', 'Makassar'])[1 + ((gs - 1) % 12)] AS city,
    (ARRAY['Jabodetabek', 'West Java', 'Central Java', 'East Java', 'Bali Nusra', 'Sumatra', 'Kalimantan', 'Sulawesi'])[1 + ((gs - 1) % 8)] AS region,
    CASE WHEN gs % 40 = 0 THEN FALSE ELSE TRUE END AS is_active
FROM generate_series(1, 500) gs;

INSERT INTO vehicles (
    dealer_id, vehicle_code, brand, model, vehicle_type, vehicle_category,
    production_year, is_new, price
)
SELECT
    1 + ((gs - 1) % 500) AS dealer_id,
    'VH' || LPAD(gs::text, 7, '0') AS vehicle_code,
    CASE WHEN gs % 10 < 8 THEN (ARRAY['Honda', 'Yamaha', 'Suzuki', 'Kawasaki'])[1 + ((gs - 1) % 4)]
         ELSE (ARRAY['Toyota', 'Daihatsu', 'Mitsubishi', 'Honda'])[1 + ((gs - 1) % 4)]
    END AS brand,
    CASE WHEN gs % 10 < 8 THEN 'Motor Model ' || (1 + (gs % 30))
         ELSE 'Car Model ' || (1 + (gs % 20))
    END AS model,
    CASE WHEN gs % 10 < 8 THEN 'MOTORCYCLE' ELSE 'CAR' END AS vehicle_type,
    CASE
        WHEN gs % 10 < 8 THEN (ARRAY['MATIC', 'SPORT'])[1 + (gs % 2)]
        ELSE (ARRAY['SUV', 'MPV', 'COMMERCIAL'])[1 + (gs % 3)]
    END AS vehicle_category,
    2017 + (gs % 9) AS production_year,
    CASE WHEN gs % 5 = 0 THEN FALSE ELSE TRUE END AS is_new,
    CASE WHEN gs % 10 < 8 THEN (18000000 + random() * 22000000)::numeric(18,2)
         ELSE (180000000 + random() * 250000000)::numeric(18,2)
    END AS price
FROM generate_series(1, 5000) gs;

-- =====================================================
-- 2) CREDIT APPLICATIONS
-- Distribution target:
-- APPROVED 55%, REJECTED 20%, MANUAL_REVIEW 15%, CANCELLED 5%, SUBMITTED 4%, DRAFT 1%
-- =====================================================

INSERT INTO credit_applications (
    application_number, customer_id, vehicle_id, branch_id, created_by,
    application_date, vehicle_price, dp_amount, loan_amount, approved_amount,
    tenor_months, interest_rate, status,
    submitted_at, approved_at, rejected_at, manual_review_at, cancelled_at,
    decision_reason
)
WITH app_base AS (
    SELECT
        gs,
        1 + ((random() * 9999)::int) AS customer_id,
        1 + ((random() * 4999)::int) AS vehicle_id,
        1 + ((random() * 99)::int) AS branch_id,
        1 + ((random() * 599)::int) AS created_by,
        DATE '2024-01-01' + ((random() * 895)::int) AS application_date,
        (random() * 100)::int AS status_bucket,
        (ARRAY[12, 18, 24, 30, 36])[1 + ((random() * 4)::int)] AS tenor_months
    FROM generate_series(1, 30000) gs
), priced AS (
    SELECT
        b.*,
        v.price AS vehicle_price,
        CASE
            WHEN b.status_bucket < 55 THEN 'APPROVED'
            WHEN b.status_bucket < 75 THEN 'REJECTED'
            WHEN b.status_bucket < 90 THEN 'MANUAL_REVIEW'
            WHEN b.status_bucket < 95 THEN 'CANCELLED'
            WHEN b.status_bucket < 99 THEN 'SUBMITTED'
            ELSE 'DRAFT'
        END AS status
    FROM app_base b
    JOIN vehicles v ON v.id = b.vehicle_id
), amounts AS (
    SELECT
        *,
        (vehicle_price * (0.10 + random() * 0.25))::numeric(18,2) AS dp_amount
    FROM priced
)
SELECT
    'APP' || LPAD(gs::text, 8, '0') AS application_number,
    customer_id,
    vehicle_id,
    branch_id,
    created_by,
    application_date,
    vehicle_price,
    dp_amount,
    (vehicle_price - dp_amount)::numeric(18,2) AS loan_amount,
    CASE WHEN status = 'APPROVED' THEN (vehicle_price - dp_amount)::numeric(18,2) ELSE NULL END AS approved_amount,
    tenor_months,
    (8 + random() * 10)::numeric(5,2) AS interest_rate,
    status,
    CASE WHEN status <> 'DRAFT' THEN application_date::timestamp + interval '2 hours' ELSE NULL END AS submitted_at,
    CASE WHEN status = 'APPROVED' THEN application_date::timestamp + ((4 + random() * 72)::int || ' hours')::interval ELSE NULL END AS approved_at,
    CASE WHEN status = 'REJECTED' THEN application_date::timestamp + ((4 + random() * 72)::int || ' hours')::interval ELSE NULL END AS rejected_at,
    CASE WHEN status = 'MANUAL_REVIEW' THEN application_date::timestamp + ((4 + random() * 48)::int || ' hours')::interval ELSE NULL END AS manual_review_at,
    CASE WHEN status = 'CANCELLED' THEN application_date::timestamp + ((1 + random() * 24)::int || ' hours')::interval ELSE NULL END AS cancelled_at,
    CASE
        WHEN status = 'APPROVED' THEN 'Eligible based on score and income capacity'
        WHEN status = 'REJECTED' THEN 'Rejected due to risk policy or affordability issue'
        WHEN status = 'MANUAL_REVIEW' THEN 'Needs analyst review due to borderline profile'
        WHEN status = 'CANCELLED' THEN 'Cancelled by customer or branch'
        ELSE NULL
    END AS decision_reason
FROM amounts;

-- =====================================================
-- 3) RISK ASSESSMENTS
-- Only for non-DRAFT applications.
-- Decision follows credit application status.
-- =====================================================

INSERT INTO risk_assessments (
    credit_application_id, assessed_by, risk_score, risk_level, decision,
    income_score, age_score, loan_amount_score, dbr_score, previous_payment_score,
    dbr_percentage, notes, assessed_at
)
WITH base AS (
    SELECT
        ca.id,
        ca.status,
        1 + ((random() * 399)::int) + 600 AS assessed_by,
        CASE
            WHEN ca.status = 'APPROVED' THEN 60 + ((random() * 40)::int)
            WHEN ca.status = 'MANUAL_REVIEW' THEN 40 + ((random() * 30)::int)
            WHEN ca.status = 'REJECTED' THEN ((random() * 50)::int)
            ELSE 30 + ((random() * 50)::int)
        END AS risk_score,
        (10 + random() * 65)::numeric(5,2) AS dbr_percentage,
        COALESCE(ca.approved_at, ca.rejected_at, ca.manual_review_at, ca.cancelled_at, ca.submitted_at, ca.application_date::timestamp) AS assessed_at
    FROM credit_applications ca
    WHERE ca.status <> 'DRAFT'
)
SELECT
    id AS credit_application_id,
    assessed_by,
    risk_score,
    CASE
        WHEN risk_score >= 70 THEN 'LOW'
        WHEN risk_score >= 45 THEN 'MEDIUM'
        ELSE 'HIGH'
    END AS risk_level,
    CASE
        WHEN status = 'APPROVED' THEN 'APPROVED'
        WHEN status = 'REJECTED' THEN 'REJECTED'
        ELSE 'MANUAL_REVIEW'
    END AS decision,
    LEAST(100, GREATEST(0, risk_score + ((random() * 20)::int - 10))) AS income_score,
    LEAST(100, GREATEST(0, risk_score + ((random() * 20)::int - 10))) AS age_score,
    LEAST(100, GREATEST(0, risk_score + ((random() * 20)::int - 10))) AS loan_amount_score,
    LEAST(100, GREATEST(0, risk_score + ((random() * 20)::int - 10))) AS dbr_score,
    LEAST(100, GREATEST(0, risk_score + ((random() * 20)::int - 10))) AS previous_payment_score,
    dbr_percentage,
    'Synthetic assessment for training data' AS notes,
    assessed_at
FROM base;

-- =====================================================
-- 4) INSTALLMENTS
-- Only APPROVED applications generate installments.
-- For small mode: APPROVED ~16.5k applications x avg tenor 20-24 months? Depends tenor distribution.
-- If you want exactly 360k, force tenor to 12 for all or adjust approved volume.
-- Here we follow real tenor from applications, giving a more realistic distribution.
-- =====================================================

INSERT INTO installments (
    credit_application_id, installment_number, due_date,
    amount, paid_amount, outstanding_amount,
    payment_date, days_overdue, status
)
WITH app AS (
    SELECT
        id AS credit_application_id,
        application_date,
        loan_amount,
        tenor_months
    FROM credit_applications
    WHERE status = 'APPROVED'
), inst_base AS (
    SELECT
        a.credit_application_id,
        gs AS installment_number,
        (a.application_date + (gs || ' months')::interval)::date AS due_date,
        (a.loan_amount / a.tenor_months)::numeric(18,2) AS amount,
        (random() * 100)::int AS status_bucket,
        (random() * 60)::int AS overdue_random
    FROM app a
    CROSS JOIN LATERAL generate_series(1, a.tenor_months) gs
), classified AS (
    SELECT
        *,
        CASE
            WHEN status_bucket < 65 THEN 'PAID'
            WHEN status_bucket < 80 THEN 'UNPAID'
            WHEN status_bucket < 90 THEN 'LATE'
            WHEN status_bucket < 97 THEN 'PARTIAL_PAID'
            ELSE 'DEFAULTED'
        END AS status
    FROM inst_base
)
SELECT
    credit_application_id,
    installment_number,
    due_date,
    amount,
    CASE
        WHEN status = 'PAID' THEN amount
        WHEN status = 'PARTIAL_PAID' THEN (amount * (0.25 + random() * 0.60))::numeric(18,2)
        WHEN status = 'LATE' THEN amount
        ELSE 0::numeric(18,2)
    END AS paid_amount,
    CASE
        WHEN status = 'PAID' THEN 0::numeric(18,2)
        WHEN status = 'PARTIAL_PAID' THEN (amount * (0.15 + random() * 0.75))::numeric(18,2)
        WHEN status = 'LATE' THEN 0::numeric(18,2)
        ELSE amount
    END AS outstanding_amount,
    CASE
        WHEN status = 'PAID' THEN due_date - ((random() * 5)::int)
        WHEN status = 'LATE' THEN due_date + (1 + overdue_random)
        WHEN status = 'PARTIAL_PAID' THEN due_date + ((random() * 15)::int)
        ELSE NULL
    END AS payment_date,
    CASE
        WHEN status IN ('LATE', 'DEFAULTED') THEN 1 + overdue_random
        WHEN status = 'PARTIAL_PAID' THEN (random() * 15)::int
        ELSE 0
    END AS days_overdue,
    status
FROM classified;

-- =====================================================
-- 5) PAYMENTS
-- One payment row for installments that have paid_amount > 0.
-- This is enough for payment channel analytics and slow join simulation.
-- =====================================================

INSERT INTO payments (
    installment_id, paid_by, payment_number, payment_date, payment_amount,
    payment_method, payment_channel
)
SELECT
    i.id AS installment_id,
    1 + ((random() * 999)::int) AS paid_by,
    'PAY' || LPAD(i.id::text, 10, '0') AS payment_number,
    COALESCE(i.payment_date, i.due_date) AS payment_date,
    i.paid_amount AS payment_amount,
    CASE
        WHEN random() < 0.35 THEN 'VIRTUAL_ACCOUNT'
        WHEN random() < 0.65 THEN 'TRANSFER'
        WHEN random() < 0.85 THEN 'CASH'
        ELSE 'AUTODEBIT'
    END AS payment_method,
    CASE
        WHEN random() < 0.35 THEN 'MOBILE_APP'
        WHEN random() < 0.65 THEN 'BANK'
        WHEN random() < 0.85 THEN 'BRANCH'
        ELSE 'PARTNER'
    END AS payment_channel
FROM installments i
WHERE i.paid_amount > 0;

-- =====================================================
-- 6) AUDIT LOGS
-- Mix of CREATE/SUBMIT/APPROVE/REJECT/PAYMENT/LOGIN.
-- =====================================================

INSERT INTO audit_logs (user_id, entity_name, entity_id, action, old_value, new_value, ip_address, user_agent, created_at)
SELECT
    created_by AS user_id,
    'credit_applications' AS entity_name,
    id AS entity_id,
    'CREATE' AS action,
    NULL AS old_value,
    '{"status":"DRAFT"}' AS new_value,
    '10.10.' || (id % 255) || '.' || ((id * 7) % 255) AS ip_address,
    'Mozilla/5.0 Training Browser' AS user_agent,
    application_date::timestamp AS created_at
FROM credit_applications;

INSERT INTO audit_logs (user_id, entity_name, entity_id, action, old_value, new_value, ip_address, user_agent, created_at)
SELECT
    created_by,
    'credit_applications',
    id,
    'SUBMIT',
    '{"status":"DRAFT"}',
    '{"status":"SUBMITTED"}',
    '10.20.' || (id % 255) || '.' || ((id * 11) % 255),
    'Mozilla/5.0 Training Browser',
    submitted_at
FROM credit_applications
WHERE submitted_at IS NOT NULL;

INSERT INTO audit_logs (user_id, entity_name, entity_id, action, old_value, new_value, ip_address, user_agent, created_at)
SELECT
    COALESCE(ra.assessed_by, ca.created_by),
    'credit_applications',
    ca.id,
    CASE WHEN ca.status = 'APPROVED' THEN 'APPROVE' WHEN ca.status = 'REJECTED' THEN 'REJECT' ELSE 'UPDATE' END,
    '{"status":"SUBMITTED"}',
    '{"status":"' || ca.status || '"}',
    '10.30.' || (ca.id % 255) || '.' || ((ca.id * 13) % 255),
    'Mozilla/5.0 Training Browser',
    COALESCE(ca.approved_at, ca.rejected_at, ca.manual_review_at, ca.cancelled_at, ca.submitted_at)
FROM credit_applications ca
LEFT JOIN risk_assessments ra ON ra.credit_application_id = ca.id
WHERE ca.status IN ('APPROVED', 'REJECTED', 'MANUAL_REVIEW', 'CANCELLED');

INSERT INTO audit_logs (user_id, entity_name, entity_id, action, old_value, new_value, ip_address, user_agent, created_at)
SELECT
    paid_by,
    'payments',
    id,
    'PAYMENT',
    NULL,
    '{"payment_amount":' || payment_amount || '}',
    '10.40.' || (id % 255) || '.' || ((id * 17) % 255),
    'Payment Channel Simulator',
    payment_date::timestamp
FROM payments
WHERE id % 3 = 0;

INSERT INTO audit_logs (user_id, entity_name, entity_id, action, old_value, new_value, ip_address, user_agent, created_at)
SELECT
    1 + ((random() * 999)::int),
    'users',
    1 + ((random() * 999)::int),
    'LOGIN',
    NULL,
    '{"login":"success"}',
    '10.50.' || (gs % 255) || '.' || ((gs * 19) % 255),
    'Mozilla/5.0 Training Browser',
    TIMESTAMP '2024-01-01 08:00:00' + ((random() * 895)::int || ' days')::interval + ((random() * 12)::int || ' hours')::interval
FROM generate_series(1, 20000) gs;

