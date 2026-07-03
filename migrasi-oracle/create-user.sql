-- 1. BUAT TABLESPACE (Opsional, tapi sangat disarankan agar data terpisah dari tablespace sistem)
CREATE TABLESPACE pg_migration_ts
DATAFILE 'pg_migration_data.dbf' SIZE 100M AUTOEXTEND ON NEXT 50M MAXSIZE UNLIMITED;

-- 2. BUAT USER / SCHEMA BARU
-- Catatan: Di Oracle 12c ke atas, jika menggunakan Pluggable Database (PDB), langsung jalankan ini.
-- Jika terpaksa menggunakan Container DB (CDB), nama user harus diawali c## (contoh: c##my_oracle_user)
CREATE USER fifapp_credit
IDENTIFIED BY "fifapp_credit"
DEFAULT TABLESPACE pg_migration_ts
TEMPORARY TABLESPACE TEMP;

-- 3. BERIKAN KUOTA PENYIMPANAN (Wajib agar user bisa membuat tabel dan mengisi data)
ALTER USER fifapp_credit QUOTA UNLIMITED ON pg_migration_ts;

-- 4. BERIKAN HAK AKSES DASAR UNTUK KONEKSI DAN MEMBUAT OBJEK
GRANT CREATE SESSION TO fifapp_credit; -- Mengizinkan user untuk login/konek
GRANT CREATE TABLE TO fifapp_credit;   -- Mengizinkan membuat tabel (untuk migrasi DBeaver)
GRANT CREATE VIEW TO fifapp_credit;    -- Mengizinkan membuat view (jika ada)
GRANT CREATE SEQUENCE TO fifapp_credit;-- Mengizinkan membuat sequence (pengganti SERIAL di Postgres)

-- 5. BERIKAN HAK AKSES TAMBAHAN (Opsional, jika ada fungsi/prosedur)
GRANT CREATE PROCEDURE TO fifapp_credit;
GRANT CREATE TRIGGER TO fifapp_credit;

-- 6. VERIFIKASI (Opsional: Cek apakah user sudah terbuat)
SELECT username FROM dba_users WHERE username = 'FIFAPP_CREDIT';
