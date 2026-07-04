# 28 - FIFPedy Credit Phase 1 Dashboard Redis Cache HikariCP API

Project kedua puluh delapan untuk training Data Layer. Project ini adalah copy dari project 27 (Redis cache), kemudian menambahkan konfigurasi dan observability HikariCP untuk koneksi PostgreSQL.

HikariCP tidak ditambahkan sebagai implementasi buatan sendiri. `spring-boot-starter-data-jpa` memang sudah memakai HikariCP sebagai connection pool default Spring Boot. Pada project ini dependency, konfigurasi, metrik, dan skenario demonya dibuat eksplisit agar perilaku pool dapat dibuktikan saat kelas.

## Perubahan dari Project 27

- Artifact Maven dan `spring.application.name` diubah menjadi versi `hikaricp`.
- Dependency `com.zaxxer:HikariCP` ditulis eksplisit di `pom.xml`; versinya tetap dikelola oleh Spring Boot.
- Konfigurasi pool ditambahkan di `application.properties`.
- `GET /api/database/pool` menampilkan metrik runtime dari `HikariPoolMXBean`.
- `GET /api/database/pool/hold` adalah endpoint khusus lab yang meminjam satu koneksi dan menahannya untuk durasi terbatas. Endpoint ini membuat kondisi active, idle, pending, dan batas maksimum pool terlihat tanpa mengubah business endpoint.

## Konfigurasi HikariCP

```properties
spring.datasource.hikari.pool-name=FIFPedyHikariPool
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.connection-timeout=3000
spring.datasource.hikari.validation-timeout=1000
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.max-lifetime=600000
```

Arti konfigurasi untuk lab:

- `minimum-idle=1`: setelah aplikasi siap, pool mempertahankan minimal satu koneksi idle.
- `maximum-pool-size=3`: paling banyak tiga koneksi database dipinjam secara bersamaan.
- `connection-timeout=3000`: request keempat menunggu maksimal tiga detik jika tiga koneksi sedang dipakai, lalu gagal dengan timeout.
- `pool-name`: nama yang muncul pada log Hikari dan response endpoint status.

Nilai maksimum tiga sengaja kecil untuk demo. Nilai produksi harus ditentukan dari kapasitas PostgreSQL, jumlah instance aplikasi, dan pola query; jangan menyalin angka ini ke produksi tanpa perhitungan.

## Menjalankan Project

Pastikan PostgreSQL dari project sebelumnya aktif pada konfigurasi berikut:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fifpedy_credit?currentSchema=flyway_training
spring.datasource.username=fifpedy
spring.datasource.password=fifpedy
```

Redis tetap diperlukan karena project ini mewarisi cache dashboard dari project 27.

```bash
cd "/Users/mymac/Documents/FIF25 Data Layer/28-fifpedy-credit-phase1-credit-application-dashboard-redis-cache-hikaricp-api"
docker compose up -d redis
./mvnw spring-boot:run
```

Jika Redis project 27 masih berjalan pada port `6379`, tidak perlu menjalankan container kedua; lanjutkan langsung ke `./mvnw spring-boot:run`.

Saat startup, perhatikan log Hikari. Contoh indikator yang penting:

```text
FIFPedyHikariPool - Starting...
FIFPedyHikariPool - Added connection ...
FIFPedyHikariPool - Start completed.
```

## Endpoint Lab HikariCP

```text
GET /api/database/ping
GET /api/database/pool
GET /api/database/pool/hold?durationMs=5000
```

Contoh response `GET /api/database/pool`:

```json
{
  "poolName": "FIFPedyHikariPool",
  "totalConnections": 1,
  "activeConnections": 0,
  "idleConnections": 1,
  "threadsAwaitingConnection": 0,
  "maximumPoolSize": 3,
  "minimumIdle": 1,
  "connectionTimeoutMs": 3000,
  "status": "running"
}
```

Metrik dibaca langsung dari Hikari. `totalConnections = activeConnections + idleConnections` pada saat snapshot diambil.

## Skenario Testing Before dan After

Skenario ini membandingkan keadaan pool sebelum request paralel dan saat pool dipakai. Jalankan dari terminal lain setelah aplikasi hidup.

### 1. Before: pool idle

```bash
curl -s http://localhost:8080/api/database/pool
```

Expected utama:

```text
poolName=FIFPedyHikariPool
activeConnections=0
idleConnections>=1
maximumPoolSize=3
status=running
```

Ini membuktikan data source yang aktif adalah HikariCP dan koneksi tidak sedang dipinjam. Jumlah `totalConnections` bisa lebih dari satu karena Flyway, Hibernate, atau request sebelumnya juga menggunakan pool.

### 2. After satu request: koneksi dikembalikan ke pool

Jalankan request tahan-koneksi dua kali secara berurutan:

```bash
curl -s "http://localhost:8080/api/database/pool/hold?durationMs=100"
curl -s "http://localhost:8080/api/database/pool/hold?durationMs=100"
curl -s http://localhost:8080/api/database/pool
```

Expected log aplikasi:

```text
[HIKARI CONNECTION ACQUIRED] pool=FIFPedyHikariPool ...
[HIKARI CONNECTION RETURNED] pool=FIFPedyHikariPool ...
```

Setelah kedua request selesai, status kembali ke `activeConnections=0` dan koneksi menjadi `idleConnections`. Ini menunjukkan koneksi dipinjam lalu dikembalikan ke pool, bukan dibiarkan terbuka oleh request.

### 3. After request paralel: tiga koneksi aktif

Jalankan tiga request di background. Masing-masing menahan satu koneksi selama enam detik.

```bash
for i in 1 2 3; do
  curl -s "http://localhost:8080/api/database/pool/hold?durationMs=6000" > "/tmp/hikari-hold-$i.json" &
done

sleep 1
curl -s http://localhost:8080/api/database/pool
```

Expected snapshot saat ketiga request masih berjalan:

```text
totalConnections=3
activeConnections=3
idleConnections=0
threadsAwaitingConnection=0
maximumPoolSize=3
```

Jika sebelumnya pool sudah memiliki koneksi idle, Hikari menambah koneksi sampai batas tiga. Endpoint hold melakukan `select 1` memakai koneksi yang sama, sehingga satu request hold hanya meminjam satu koneksi.

### 4. Buktikan batas pool dan timeout

Saat tiga request pada langkah 3 masih berjalan, jalankan request keempat di background, lalu cek pool sebelum timeout tiga detik selesai:

```bash
curl -s -o /tmp/hikari-hold-4.json -w "%{http_code}\\n" \
  "http://localhost:8080/api/database/pool/hold?durationMs=100" &

sleep 1
curl -s http://localhost:8080/api/database/pool
```

Expected pada snapshot kedua ada `threadsAwaitingConnection=1`. Request keempat kemudian menunggu sekitar tiga detik lalu error 500 dengan penyebab Hikari timeout, misalnya:

```text
Connection is not available, request timed out after 3000ms
```

Setelah tiga request awal selesai, cek kembali:

```bash
wait
curl -s http://localhost:8080/api/database/pool
```

Expected akhir:

```text
activeConnections=0
idleConnections=3
threadsAwaitingConnection=0
```

## Ringkasan Before vs After

| Keadaan | Before | After |
| --- | --- | --- |
| Pool | `active=0`, minimal satu `idle` | Tiga hold paralel: `active=3`, `idle=0` |
| Batas koneksi | Konfigurasi `maximumPoolSize=3` | Request keempat menunggu lalu timeout dalam 3000 ms |
| Setelah request selesai | - | `active=0`; koneksi kembali menjadi `idle`, siap dipakai ulang |
| Bukti runtime | Nama pool dan metrik endpoint | Log acquire/return, metrik MXBean, dan timeout Hikari |

## Endpoint Business yang Tetap Ada

Fitur Redis dari project 27 tetap tersedia, termasuk:

```text
GET  /api/credit-applications/dashboard?branchId=10
POST /api/credit-applications
POST /api/customers
GET  /api/customers/{id}
POST /api/vehicles
GET  /api/vehicles/{id}
```

Gunakan dashboard request berulang untuk melanjutkan demo Redis cache. Gunakan endpoint `/api/database/pool` dan `/api/database/pool/hold` khusus untuk melihat HikariCP secara eksplisit.
