package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByCompanyOrderByCreatedAtDesc(Company company);

    Long countByCompany(Company company);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.company = :company")
    BigDecimal getTotalSalesByCompany(Company company);


    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.company = :company AND DATE(b.createdAt) = CURRENT_DATE")
    BigDecimal getTodaySalesByCompany(Company company);

    List<Bill> findTop5ByCompanyOrderByCreatedAtDesc(Company company);

    @Query("""
        SELECT b FROM Bill b
        WHERE b.company = :company
        AND b.createdAt BETWEEN :fromDate AND :toDate
        ORDER BY b.createdAt DESC
        """)
    List<Bill> findBillsByCompanyAndDateRange(
            @Param("company") Company company,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    
}