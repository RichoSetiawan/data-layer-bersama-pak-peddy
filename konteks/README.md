# 25 - FIFPedy Credit Phase 1 Credit Application Join Fetch API

Project kedua puluh lima untuk training Data Layer.

Target step ini:

- Melanjutkan project 24 yang menunjukkan masalah N+1 query.
- Memperbaiki N+1 pada list credit application.
- Memakai JPQL `join fetch` di Spring Data JPA repository.
- Belum menambahkan pagination atau sorting.

## Endpoint Demo

```bash
curl http://localhost:8080/api/demo/join-fetch/credit-applications
```

Endpoint ini memakai alur list yang sudah diperbaiki:

```text
CreditApplicationRepository.findAllWithCustomerAndVehicle()
        |
        v
JPQL join fetch customer dan vehicle
        |
        v
List<CreditApplicationResponse>
```

## Repository

```java
@Query("""
        select creditApplication
        from CreditApplication creditApplication
        join fetch creditApplication.customer
        join fetch creditApplication.vehicle
        """)
List<CreditApplication> findAllWithCustomerAndVehicle();
```

Filter status juga memakai join fetch:

```java
@Query("""
        select creditApplication
        from CreditApplication creditApplication
        join fetch creditApplication.customer
        join fetch creditApplication.vehicle
        where creditApplication.status = :status
        """)
List<CreditApplication> findByStatusWithCustomerAndVehicle(String status);
```

## Efeknya

Sebelum join fetch, list 2 data bisa menghasilkan pola:

```text
select ... from credit_applications
select ... from customers where id=?
select ... from vehicles where id=?
select ... from customers where id=?
select ... from vehicles where id=?
```

Setelah join fetch, Hibernate bisa mengambil data utama dan relasi dalam satu query:

```text
select ...
from credit_applications
join customers
join vehicles
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
```
