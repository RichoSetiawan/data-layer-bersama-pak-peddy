# 26 - FIFPedy Credit Phase 1 Credit Application Dashboard API

Project kedua puluh enam untuk training Data Layer.

Target step ini:

- Menambahkan endpoint analitikal untuk dashboard credit application.
- Menambahkan kolom `branch_id` ke table `credit_applications`.
- Menjalankan beberapa query agregasi berdasarkan `branchId`.
- Menyiapkan endpoint yang cocok untuk demo Redis caching di step berikutnya.
- Belum menambahkan Redis di project ini.

## Migration Baru

```text
src/main/resources/db/migration/V5__add_branch_id_to_credit_applications.sql
```

Isi utama:

```sql
alter table credit_applications
add column branch_id bigint not null default 10;

create index idx_credit_applications_branch_id on credit_applications (branch_id);
create index idx_credit_applications_branch_status on credit_applications (branch_id, status);
```

## Endpoint Baru

Credit application dashboard:

```bash
curl "http://localhost:8080/api/credit-applications/dashboard?branchId=10"
```

## Apa yang Dihitung

Dashboard menjalankan beberapa agregasi:

```text
totalApplications
totalLoanAmount
averageLoanAmount
minimumLoanAmount
maximumLoanAmount
applicationsByStatus
applicationsByTenorMonth
applicationsByVehicleBrand
```

## Kenapa Cocok untuk Demo Redis

Endpoint ini lebih berat dibanding detail/list biasa karena menjalankan beberapa query agregasi:

```text
1 query summary total
1 query group by status
1 query group by tenor_month
1 query group by vehicle brand
```

Untuk data yang makin banyak dan dashboard yang sering dibuka, response seperti ini cocok untuk dicache:

```text
branchId=10 -> hasil dashboard
```

Di step Redis berikutnya, service method `getDashboard(branchId)` bisa diberi cache.

## Create Credit Application dengan Branch

`branchId` optional. Jika tidak dikirim, default di service adalah `10`.

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

## Endpoint Bisnis Tetap Ada

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
