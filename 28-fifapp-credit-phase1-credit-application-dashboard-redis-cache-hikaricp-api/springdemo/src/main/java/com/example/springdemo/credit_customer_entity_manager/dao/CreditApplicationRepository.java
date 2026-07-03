package com.example.springdemo.credit_customer_entity_manager.dao;

import com.example.springdemo.credit_customer_entity_manager.entity.CreditApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
    List<CreditApplication> findByStatus(String status);

//    @Override
//    @EntityGraph(attributePaths = {"customer", "vehicle"})
//    List<CreditApplication> findAll();

    @Query("""
        select creditApplication
        from CreditApplication creditApplication
        join fetch creditApplication.customer
        join fetch creditApplication.vehicle
        """)
    List<CreditApplication> findAllWithCustomerAndVehicle();

    @Query("""
        select creditApplication
        from CreditApplication creditApplication
        join fetch creditApplication.customer
        join fetch creditApplication.vehicle
        where creditApplication.status = :status
        """)
    List<CreditApplication> findByStatusWithCustomerAndVehicle(String status);

    @Query(value = """
            select
                count(ca.id),
                coalesce(sum(ca.loan_amount), 0),
                coalesce(avg(ca.loan_amount), 0),
                coalesce(min(ca.loan_amount), 0),
                coalesce(max(ca.loan_amount), 0)
            from credit_applications ca
            where ca.branch_id = :branchId
            """, nativeQuery = true)
    Object[] calculateDashboardSummary(@Param("branchId") Long branchId);

    @Query(value = """
            select
                ca.status,
                count(ca.id),
                coalesce(sum(ca.loan_amount), 0)
            from credit_applications ca
            where ca.branch_id = :branchId
            group by ca.status
            order by count(ca.id) desc
            """, nativeQuery = true)
    List<Object[]> calculateDashboardByStatus(@Param("branchId") Long branchId);

    @Query(value = """
            select
                cast(ca.tenor_month as varchar),
                count(ca.id),
                coalesce(sum(ca.loan_amount), 0)
            from credit_applications ca
            where ca.branch_id = :branchId
            group by ca.tenor_month
            order by ca.tenor_month
            """, nativeQuery = true)
    List<Object[]> calculateDashboardByTenorMonth(@Param("branchId") Long branchId);

    @Query(value = """
            select
                v.brand,
                count(ca.id),
                coalesce(sum(ca.loan_amount), 0)
            from credit_applications ca
            join vehicles v on v.id = ca.vehicle_id
            where ca.branch_id = :branchId
            group by v.brand
            order by count(ca.id) desc
            """, nativeQuery = true)
    List<Object[]> calculateDashboardByVehicleBrand(@Param("branchId") Long branchId);


}
