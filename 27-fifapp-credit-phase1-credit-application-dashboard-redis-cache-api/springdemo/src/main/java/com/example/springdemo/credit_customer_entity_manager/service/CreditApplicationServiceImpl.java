package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dao.CreditApplicationRepository;
import com.example.springdemo.credit_customer_entity_manager.dao.CustomerRepository;
import com.example.springdemo.credit_customer_entity_manager.dao.VehicleRepository;
import com.example.springdemo.credit_customer_entity_manager.dto.*;
import com.example.springdemo.credit_customer_entity_manager.entity.CreditApplication;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import com.example.springdemo.credit_customer_entity_manager.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditApplicationServiceImpl implements CreditApplicationService {

    private static final String SUBMITTED_STATUS = "SUBMITTED";

    private static final Long DEFAULT_BRANCH_ID = 10L;

    private final CreditApplicationRepository creditApplicationRepository;

    private final CustomerRepository customerRepository;

    private final VehicleRepository vehicleRepository;

    private final DashboardCacheService dashboardCacheService;

    @Override
    public CreditApplicationResponse createCreditApplication(CreateCreditApplicationRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getVehicleId()));

        CreditApplication creditApplication = CreditApplication.builder()
                .customer(customer)
                .vehicle(vehicle)
                .loanAmount(request.getLoanAmount())
                .tenorMonth(request.getTenorMonth())
                .status(SUBMITTED_STATUS)
                .build();

        CreditApplication savedCreditApplication = creditApplicationRepository.save(creditApplication);

        return toResponse(savedCreditApplication);
    }

    @Override
    public CreditApplicationResponse getCreditApplicationById(Long id) {
        return creditApplicationRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    public List<CreditApplicationResponse> getAllCreditApplications(){
        return creditApplicationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CreditApplicationResponse> getCreditApplicationsByStatus(String status){
        List<CreditApplication> creditApplications;
        if(status == null || status.trim().isEmpty()){
            creditApplications = creditApplicationRepository.findAll();
        } else {
            creditApplications = creditApplicationRepository.findByStatus(status.toUpperCase());
        }
        return creditApplications.stream().map(this::toResponse).toList();
    }

    @Override
    public List<CreditApplicationResponse> findAllWithCustomerAndVehicle(){
        return creditApplicationRepository.findAllWithCustomerAndVehicle()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CreditApplicationResponse> findByStatusWithCustomerAndVehicle(String string){
        List<CreditApplication> creditApplications;
        if(string == null || string.trim().isEmpty()){
            creditApplications = creditApplicationRepository.findAllWithCustomerAndVehicle();
        } else {
            creditApplications = creditApplicationRepository.findByStatusWithCustomerAndVehicle(string.toUpperCase());
        }
        return creditApplications.stream().map(this::toResponse).toList();
    }

    @Override
    public CreditApplicationSummaryResponse getSummary(Long id) {
        return creditApplicationRepository.findById(id)
                .map(this::toSummaryResponse)
                .orElse(null);
    }

    private CreditApplicationSummaryResponse toSummaryResponse(CreditApplication creditApplication) {
        Customer customer = creditApplication.getCustomer();
        Vehicle vehicle = creditApplication.getVehicle();
        String vehicleName = vehicle.getBrand() + " " + vehicle.getModel();

        return new CreditApplicationSummaryResponse(
                creditApplication.getId(),
                creditApplication.getStatus(),
                customer.getFullName(),
                customer.getPhoneNumber(),
                vehicleName,
                vehicle.getPlateNumber(),
                creditApplication.getLoanAmount(),
                creditApplication.getTenorMonth()
        );
    }

    private CreditApplicationResponse toResponse(CreditApplication creditApplication) {
        Customer customer = creditApplication.getCustomer();
        Vehicle vehicle = creditApplication.getVehicle();

        return new CreditApplicationResponse(
                creditApplication.getId(),
                customer.getId(),
                customer.getFullName(),
                vehicle.getId(),
                vehicle.getPlateNumber(),
                creditApplication.getLoanAmount(),
                creditApplication.getTenorMonth(),
                creditApplication.getStatus()
        );
    }

    @Override
    public CreditApplicationDashboardResponse getDashboard(Long branchId) {
        long startTime = System.nanoTime();

        CreditApplicationDashboardResponse cachedDashboard = dashboardCacheService.get(branchId);

        if (cachedDashboard != null) {
            log.info("[DASHBOARD SERVICE] branchId={} source=redis elapsedMs={}",
                    branchId,
                    elapsedMs(startTime));
            return cachedDashboard;
        }

        CreditApplicationDashboardResponse dashboard = loadDashboardFromDatabase(branchId);
        dashboardCacheService.put(branchId, dashboard);
        log.info("[DASHBOARD SERVICE] branchId={} source=database elapsedMs={}",
                branchId,
                elapsedMs(startTime));

        return dashboard;
    }

    private CreditApplicationDashboardResponse loadDashboardFromDatabase(Long branchId) {
        long startTime = System.nanoTime();
        Object[] summary = unwrapSingleRow(creditApplicationRepository.calculateDashboardSummary(branchId));

        CreditApplicationDashboardResponse dashboard = new CreditApplicationDashboardResponse(
                branchId,
                toLong(summary[0]),
                toBigDecimal(summary[1]),
                toBigDecimal(summary[2]),
                toBigDecimal(summary[3]),
                toBigDecimal(summary[4]),
                toDashboardGroups(creditApplicationRepository.calculateDashboardByStatus(branchId)),
                toDashboardGroups(creditApplicationRepository.calculateDashboardByTenorMonth(branchId)),
                toDashboardGroups(creditApplicationRepository.calculateDashboardByVehicleBrand(branchId))
        );

        log.info("[DATABASE QUERY] dashboard aggregation branchId={} queryCount=4 elapsedMs={}",
                branchId,
                elapsedMs(startTime));
        return dashboard;
    }

    private Long getBranchIdOrDefault(Long branchId) {
        if (branchId == null) {
            return DEFAULT_BRANCH_ID;
        }

        return branchId;
    }

    private List<DashboardGroupResponse> toDashboardGroups(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new DashboardGroupResponse(
                        String.valueOf(row[0]),
                        toLong(row[1]),
                        toBigDecimal(row[2])
                ))
                .toList();
    }

    private Object[] unwrapSingleRow(Object result) {
        if (result instanceof Object[] row && row.length == 1 && row[0] instanceof Object[] nestedRow) {
            return nestedRow;
        }

        return (Object[]) result;
    }

    private Long toLong(Object value) {
        if (value instanceof BigInteger bigInteger) {
            return bigInteger.longValue();
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(String.valueOf(value));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (value instanceof BigInteger bigInteger) {
            return new BigDecimal(bigInteger);
        }

        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        return new BigDecimal(String.valueOf(value));
    }

    private long elapsedMs(long startTime) {
        return Duration.ofNanos(System.nanoTime() - startTime).toMillis();
    }
}
