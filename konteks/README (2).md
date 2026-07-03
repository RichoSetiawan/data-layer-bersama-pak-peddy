# 27 - FIFPedy Credit Phase 1 Dashboard Redis Cache API

Project kedua puluh tujuh untuk training Data Layer.

Project ini adalah copy dari project `26-fifpedy-credit-phase1-credit-application-dashboard-api`, lalu ditambahkan Redis cache untuk endpoint:

```bash
GET /api/credit-applications/dashboard?branchId=10
```

Tujuan lab:

- Peserta melihat kondisi sebelum cache: endpoint dashboard menjalankan query agregasi ke PostgreSQL.
- Peserta melihat kondisi cache miss: request pertama belum punya data di Redis, sehingga tetap query database lalu menyimpan response ke Redis.
- Peserta melihat kondisi cache hit: request berikutnya mengambil response dari Redis, sehingga query agregasi tidak dijalankan lagi.
- Peserta melihat cache invalidation sederhana saat `POST /api/credit-applications` berhasil membuat data baru.

## Cara Project Ini Dibuat dari Project 26

Dari folder:

```bash
cd "/Users/mymac/Training/Utopiq/MT FIF Batch 25/Data Layer/spring-boot"
```

Copy project 26 menjadi project 27:

```bash
cp -R \
  "26-fifpedy-credit-phase1-credit-application-dashboard-api" \
  "27-fifpedy-credit-phase1-credit-application-dashboard-redis-cache-api"
```

Masuk ke project baru:

```bash
cd "27-fifpedy-credit-phase1-credit-application-dashboard-redis-cache-api"
```

Bersihkan build artifact hasil copy:

```bash
rm -rf target
```

## Perubahan yang Ditambahkan

### 1. Dependency Redis

Di `pom.xml`, artifact project diganti agar jelas ini versi Redis cache:

```xml
<artifactId>fifpedy-credit-phase1-credit-application-dashboard-redis-cache-api</artifactId>
<name>fifpedy-credit-phase1-credit-application-dashboard-redis-cache-api</name>
```

Dependency baru:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 2. Konfigurasi Redis

Di `src/main/resources/application.properties`:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2s

app.redis.dashboard-cache-prefix=dashboard:branch
app.redis.dashboard-cache-ttl=60s
```

TTL dibuat `60s` agar mudah didemokan di kelas. Setelah 60 detik, key expired dan request berikutnya akan menjadi cache miss lagi.

### 3. Docker Compose Redis

File baru:

```text
docker-compose.yml
```

Isi utamanya menjalankan Redis di port `6379`:

```yaml
services:
  redis:
    image: redis:7.4-alpine
    container_name: fifpedy-dashboard-redis
    ports:
      - "6379:6379"
```

### 4. Service Cache

File baru:

```text
src/main/java/id/co/fifpedy/creditphase1creditapplicationdashboard/service/DashboardCacheService.java
```

Service ini menggunakan:

- `RedisTemplate<String, String>` untuk baca/tulis Redis.
- `ObjectMapper` untuk serialize response dashboard menjadi JSON.
- Key Redis dengan format:

```text
dashboard:branch:{branchId}
```

Contoh untuk `branchId=10`:

```text
dashboard:branch:10
```

Log penting:

```text
[REDIS CACHE MISS]
[REDIS CACHE HIT]
[REDIS CACHE PUT]
[REDIS CACHE EVICT]
[REDIS CACHE UNAVAILABLE]
```

### 5. Dashboard Service Menggunakan Cache

File yang diubah:

```text
src/main/java/id/co/fifpedy/creditphase1creditapplicationdashboard/service/CreditApplicationServiceImpl.java
```

Flow baru method `getDashboard(branchId)`:

```text
1. Cek Redis dengan key dashboard:branch:{branchId}
2. Jika ada, return response dari Redis
3. Jika tidak ada, query database
4. Simpan response database ke Redis
5. Return response
```

Saat `POST /api/credit-applications` berhasil, cache untuk branch terkait dihapus:

```text
dashboardCacheService.evict(savedCreditApplication.getBranchId(), "credit-application-created");
```

Ini penting agar dashboard tidak membaca data lama setelah ada credit application baru.

### 6. Logging Durasi Request

File baru:

```text
src/main/java/id/co/fifpedy/creditphase1creditapplicationdashboard/common/ApiRequestLoggingFilter.java
```

Log request akan terlihat seperti:

```text
[HTTP REQUEST] method=GET path=/api/credit-applications/dashboard query=branchId=10 status=200 elapsedMs=123
```

Log service dashboard:

```text
[DATABASE QUERY] dashboard aggregation branchId=10 queryCount=4 elapsedMs=...
[DASHBOARD SERVICE] branchId=10 source=database elapsedMs=...
[DASHBOARD SERVICE] branchId=10 source=redis elapsedMs=...
```

## Menjalankan Lab

### 1. Pastikan PostgreSQL Project Sebelumnya Sudah Jalan

Project ini masih memakai database dan schema yang sama dengan project 26:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fifpedy_credit?currentSchema=flyway_training
spring.datasource.username=fifpedy
spring.datasource.password=fifpedy
```

### 2. Jalankan Redis

Dari root project 27:

```bash
docker compose up -d redis
```

Cek container:

```bash
docker compose ps
```

Opsional, cek Redis:

```bash
docker exec -it fifpedy-dashboard-redis redis-cli ping
```

Expected:

```text
PONG
```

### 3. Jalankan Spring Boot

```bash
./mvnw spring-boot:run
```

### 4. Request Pertama: Cache Miss

```bash
curl "http://localhost:8080/api/credit-applications/dashboard?branchId=10"
```

Expected log:

```text
[REDIS CACHE MISS] key=dashboard:branch:10 ...
[DATABASE QUERY] dashboard aggregation branchId=10 queryCount=4 ...
[REDIS CACHE PUT] key=dashboard:branch:10 ttl=PT1M ...
[DASHBOARD SERVICE] branchId=10 source=database ...
```

Artinya:

- Redis belum punya key.
- Aplikasi menjalankan 4 query agregasi dashboard.
- Response disimpan ke Redis.

### 5. Request Kedua: Cache Hit

Jalankan command yang sama sebelum TTL 60 detik habis:

```bash
curl "http://localhost:8080/api/credit-applications/dashboard?branchId=10"
```

Expected log:

```text
[REDIS CACHE HIT] key=dashboard:branch:10 ...
[DASHBOARD SERVICE] branchId=10 source=redis ...
```

Yang harus diperhatikan:

- Tidak ada log `[DATABASE QUERY]`.
- Tidak ada SQL agregasi dashboard dari Hibernate.
- `elapsedMs` biasanya lebih kecil.

### 6. Cek Isi Key Redis

```bash
docker exec -it fifpedy-dashboard-redis redis-cli GET dashboard:branch:10
```

Cek TTL:

```bash
docker exec -it fifpedy-dashboard-redis redis-cli TTL dashboard:branch:10
```

### 7. Demo Expired Cache

Tunggu lebih dari 60 detik, lalu request lagi:

```bash
curl "http://localhost:8080/api/credit-applications/dashboard?branchId=10"
```

Expected kembali menjadi:

```text
[REDIS CACHE MISS]
[DATABASE QUERY]
[REDIS CACHE PUT]
```

### 8. Demo Cache Eviction Saat Create Data Baru

Buat credit application baru untuk `branchId=10`:

```bash
curl -i -X POST http://localhost:8080/api/credit-applications \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 3,
    "vehicleId": 3,
    "branchId": 10,
    "loanAmount": 18000000,
    "tenorMonth": 24
  }'
```

Expected log:

```text
[REDIS CACHE EVICT] key=dashboard:branch:10 deleted=true reason=credit-application-created
```

Request dashboard lagi:

```bash
curl "http://localhost:8080/api/credit-applications/dashboard?branchId=10"
```

Expected:

```text
[REDIS CACHE MISS]
[DATABASE QUERY]
[REDIS CACHE PUT]
```

## Demo Tanpa Redis

Untuk menunjukkan fallback saat Redis mati:

```bash
docker compose stop redis
```

Lalu request:

```bash
curl "http://localhost:8080/api/credit-applications/dashboard?branchId=10"
```

Expected log:

```text
[REDIS CACHE UNAVAILABLE] key=dashboard:branch:10 action=read-from-db ...
[DATABASE QUERY] dashboard aggregation branchId=10 queryCount=4 ...
[REDIS CACHE PUT_FAILED] key=dashboard:branch:10 action=skip-cache ...
[DASHBOARD SERVICE] branchId=10 source=database ...
```

Artinya aplikasi tetap bisa berjalan, tetapi cache tidak aktif karena Redis tidak tersedia.

## Endpoint yang Dipakai Lab

```text
GET  /api/credit-applications/dashboard?branchId=10
POST /api/credit-applications
```

Endpoint dari project sebelumnya tetap ada:

```text
POST /api/customers
GET  /api/customers/{id}

POST /api/vehicles
GET  /api/vehicles/{id}

POST /api/credit-applications
GET  /api/credit-applications/{id}
GET  /api/credit-applications
GET  /api/credit-applications?status=SUBMITTED
GET  /api/credit-applications/{id}/summary
GET  /api/credit-applications/dashboard?branchId=10
```

## Ringkasan Before vs After Cache

Before cache atau saat cache miss:

```text
Request -> Redis MISS -> Database Query 4x -> Redis PUT -> Response
```

After cache atau saat cache hit:

```text
Request -> Redis HIT -> Response
```

Hal utama yang perlu peserta amati di console:

```text
source=database
source=redis
[DATABASE QUERY]
[REDIS CACHE MISS]
[REDIS CACHE HIT]
elapsedMs
```
