-- # 1. Customers

-- ## Pertanyaan 1

-- Tim analis sering mencari customer berdasarkan kota, misalnya:

-- ```sql

explain analyze SELECT *
FROM customers
WHERE city = 'Bogor';
-- ```

-- Buat index agar pencarian customer berdasarkan `city` lebih cepat.
-- Jawaban:
create index idx_customers_city on customers(city);

-- ## Pertanyaan 2

-- Tim risk sering memfilter customer berdasarkan kelompok penghasilan, misalnya:

-- ```sql
explain analyze SELECT *
FROM customers
WHERE income_range = 'HIGH';
-- ```
-- Buat index agar filter berdasarkan `income_range` lebih cepat.
-- Jawaban
create index idx_customers_income_range on customers(income_range);

-- # 2. Dealers

-- ## Pertanyaan 3

-- Tim bisnis sering mencari dealer berdasarkan kombinasi kota dan region, misalnya:

-- ```sql
explain analyze SELECT *
FROM dealers
WHERE city = 'Jakarta'
AND region = 'Jabodetabek';
-- ```

-- Buat composite index untuk mempercepat query tersebut.

-- ### Jawaban
create index idx_dealers_city_region on dealers(city, region);

-- # 3. Vehicles

-- ## Pertanyaan 4

-- Aplikasi sering menampilkan daftar kendaraan milik dealer tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM vehicles
WHERE dealer_id = 10;
-- ```

-- Buat index untuk mempercepat pencarian kendaraan berdasarkan `dealer_id`.

-- ### Jawaban
--"Pada pertanyaan ini adalah pertanyaan jebakan karena saat pembuatan primary key index otomatis terbuat dengan jenis b-tree index"

-- ## Pertanyaan 5

-- User sering mencari kendaraan berdasarkan brand dan model, misalnya:

-- ```sql
explain analyze SELECT *
FROM vehicles
WHERE brand = 'Toyota'
AND model = 'Car Model 10';
-- ```

-- Buat composite index untuk mempercepat pencarian berdasarkan `brand` dan `model`.

-- ### Jawaban
CREATE INDEX idx_vehicles_brand_model ON vehicles (brand, model);

-- ## Pertanyaan 6

-- Tim produk sering memfilter kendaraan berdasarkan tipe dan kategori kendaraan, misalnya:

-- ```sql
explain analyze SELECT *
FROM vehicles
WHERE vehicle_type = 'Car'
AND vehicle_category = 'MPV';
-- ```

-- Buat composite index untuk mempercepat filter berdasarkan `vehicle_type` dan `vehicle_category`.

-- ### Jawaban
CREATE INDEX idx_vehicles_type_category ON vehicles (vehicle_type, vehicle_category);

-- # 4. Credit Applications

-- ## Pertanyaan 7

-- Aplikasi sering mencari semua pengajuan kredit milik customer tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE customer_id = 1001;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `customer_id`.

-- ### Jawaban
CREATE INDEX idx_credit_apps_customer_id ON credit_applications (customer_id);

-- ## Pertanyaan 8

-- Aplikasi sering mencari pengajuan kredit berdasarkan kendaraan tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE vehicle_id = 501;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `vehicle_id`.
-- Jawaban
CREATE INDEX idx_credit_apps_vehicle_id ON credit_applications (vehicle_id);

-- ## Pertanyaan 9

-- Tim cabang sering melihat daftar pengajuan kredit di branch tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE branch_id = 3;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `branch_id`.

-- ### Jawaban
CREATE INDEX idx_credit_apps_branch_id ON credit_applications (branch_id);

-- ## Pertanyaan 10

-- Supervisor ingin melihat pengajuan kredit yang dibuat oleh user tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE created_by = 15;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `created_by`.

-- ### Jawaban
CREATE INDEX idx_credit_apps_created_by ON credit_applications (created_by);

-- ## Pertanyaan 11

-- Tim reporting sering mengambil data pengajuan kredit berdasarkan tanggal aplikasi, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE application_date = DATE '2026-06-01';
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `application_date`.

-- ### Jawaban
CREATE INDEX idx_credit_apps_app_date ON credit_applications (application_date);

-- ## Pertanyaan 12

-- Tim operation sering memfilter pengajuan kredit berdasarkan status, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE status = 'MANUAL_REVIEW';
-- ```

-- Buat index untuk mempercepat filter berdasarkan `status`.

-- ### Jawaban
CREATE INDEX idx_credit_apps_status ON credit_applications (status);

-- ## Pertanyaan 13

-- Dashboard cabang sering menampilkan daftar aplikasi berdasarkan cabang, rentang tanggal, dan status, misalnya:

-- ```sql
explain analyze SELECT *
FROM credit_applications
WHERE branch_id = 3
AND application_date BETWEEN DATE '2026-06-01' AND DATE '2026-06-30'
AND status = 'APPROVED';
-- ```

-- Buat composite index untuk mempercepat query dashboard tersebut.

-- ### Jawaban
-- Menaruh kolom kesamaan (=) di depan, kemudian kolom rentang (BETWEEN) di akhir
CREATE INDEX idx_credit_apps_dash_filter ON credit_applications (branch_id, status, application_date);

-- # 5. Risk Assessments

-- ## Pertanyaan 14

-- Supervisor ingin melihat hasil risk assessment yang dilakukan oleh assessor tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM risk_assessments
WHERE assessed_by = 972;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `assessed_by`.

-- ### Jawaban
CREATE INDEX idx_risk_assess_assessed_by ON risk_assessments (assessed_by);

-- ## Pertanyaan 15

-- Tim risk sering menganalisis hasil keputusan assessment, misalnya:

-- ```sql
explain analyze SELECT *
FROM risk_assessments
WHERE decision = 'APPROVED';
-- ```

-- Buat index untuk mempercepat filter berdasarkan `decision`.

-- ### Jawaban
CREATE INDEX idx_risk_assess_decision ON risk_assessments (decision);

-- ## Pertanyaan 16

-- Tim risk ingin melihat data berdasarkan level risiko, misalnya:

-- ```sql
explain analyze SELECT *
FROM risk_assessments
WHERE risk_level = 'HIGH';
-- ```

-- Buat index untuk mempercepat filter berdasarkan `risk_level`.
-- Jawaban
CREATE INDEX idx_risk_assess_risk_level ON risk_assessments (risk_level);

-- ## Pertanyaan 17

-- Tim audit sering mencari hasil assessment berdasarkan waktu assessment, misalnya:

-- ```sql
explain analyze SELECT *
FROM risk_assessments
WHERE assessed_at >= TIMESTAMP '2026-06-01 00:00:00';
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `assessed_at`.

-- ### Jawaban
CREATE INDEX idx_risk_assess_assessed_at ON risk_assessments (assessed_at);

-- # 6. Installments

-- ## Pertanyaan 18

-- Aplikasi sering menampilkan daftar cicilan dari satu credit application tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM installments
WHERE credit_application_id = 22344;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `credit_application_id`.

-- ### Jawaban
CREATE INDEX idx_installments_app_id ON installments (credit_application_id);

-- ## Pertanyaan 19

-- Tim collection sering mencari cicilan berdasarkan tanggal jatuh tempo, misalnya:

-- ```sql
explain analyze SELECT *
FROM installments
WHERE due_date = DATE '2026-06-25';
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `due_date`.

-- ### Jawaban
CREATE INDEX idx_installments_due_date ON installments (due_date);

-- ## Pertanyaan 20

-- Tim collection sering memfilter cicilan berdasarkan status, misalnya:

-- ```sql
explain analyze SELECT *
FROM installments
WHERE status = 'UNPAID';
-- ```

-- Buat index untuk mempercepat filter berdasarkan `status`.

-- ### Jawaban
CREATE INDEX idx_installments_status ON installments (status);

-- ## Pertanyaan 21

-- Tim collection sering mencari cicilan yang belum dibayar dan sudah jatuh tempo, misalnya:

-- ```sql
explain analyze SELECT *
FROM installments
WHERE status = 'UNPAID'
AND due_date <= DATE '2026-06-25';
-- ```

-- Buat composite index untuk mempercepat query berdasarkan `status` dan `due_date`.

-- ### Jawaban
CREATE INDEX idx_installments_status_due ON installments (status, due_date);

-- # 7. Payments

-- ## Pertanyaan 22

-- Aplikasi sering mencari pembayaran untuk cicilan tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM payments
WHERE installment_id = 2001;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `installment_id`.

-- ### Jawaban
CREATE INDEX idx_payments_installment_id ON payments (installment_id);


-- ## Pertanyaan 23

-- Tim finance ingin melihat pembayaran yang diterima oleh user tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM payments
WHERE paid_by = 12;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `paid_by`.

-- ### Jawaban
CREATE INDEX idx_payments_paid_by ON payments (paid_by);

-- ## Pertanyaan 24

-- Tim finance sering mencari pembayaran berdasarkan tanggal pembayaran, misalnya:

-- ```sql
explain analyze SELECT *
FROM payments
WHERE payment_date = DATE '2026-06-25';
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `payment_date`.

-- ### Jawaban
CREATE INDEX idx_payments_payment_date ON payments (payment_date);

-- ## Pertanyaan 25

-- Tim finance sering membuat laporan pembayaran berdasarkan metode dan channel pembayaran, misalnya:

-- ```sql
explain analyze SELECT *
FROM payments
WHERE payment_method = 'TRANSFER'
AND payment_channel = 'MOBILE_APP';
-- ```

-- Buat composite index untuk mempercepat query berdasarkan `payment_method` dan `payment_channel`.

-- ### Jawaban
CREATE INDEX idx_payments_method_channel ON payments (payment_method, payment_channel);

-- # 8. Audit Logs

-- ## Pertanyaan 26

-- Tim audit sering mencari aktivitas yang dilakukan oleh user tertentu, misalnya:

-- ```sql
explain analyze SELECT *
FROM audit_logs
WHERE user_id = 5;
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `user_id`.

-- ### Jawaban
CREATE INDEX idx_audit_logs_user_id ON audit_logs (user_id);

-- ## Pertanyaan 27

-- Tim audit sering mencari log berdasarkan nama entity dan ID entity, misalnya:

-- ```sql
explain analyze SELECT *
FROM audit_logs
WHERE entity_name = 'credit_applications'
AND entity_id = 1001; 
-- ```

-- Buat composite index untuk mempercepat pencarian berdasarkan `entity_name` dan `entity_id`.

-- ### Jawaban
CREATE INDEX idx_audit_logs_entity_name_id ON audit_logs (entity_name, entity_id);

-- ## Pertanyaan 28

-- Tim audit sering memfilter log berdasarkan jenis aksi, misalnya:

-- ```sql
explain analyze SELECT *
FROM audit_logs
WHERE action = 'UPDATE';
-- ```

-- Buat index untuk mempercepat filter berdasarkan `action`.

-- ### Jawaban
CREATE INDEX idx_audit_logs_action ON audit_logs (action);


-- ## Pertanyaan 29

-- Tim audit sering mencari log berdasarkan waktu dibuatnya aktivitas, misalnya:

-- ```sql
explain analyze SELECT *
FROM audit_logs
WHERE created_at >= TIMESTAMP '2026-06-01 00:00:00';
-- ```

-- Buat index untuk mempercepat pencarian berdasarkan `created_at`.

-- ### Jawaban
CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at);

