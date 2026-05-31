package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByCompanyOrderByPurchaseDateDesc(Company company);

    @Query("""
        SELECT p FROM Purchase p
        WHERE p.company = :company
        AND p.purchaseDate BETWEEN :fromDate AND :toDate
        ORDER BY p.purchaseDate DESC
        """)
    List<Purchase> findPurchasesByCompanyAndDateRange(
            @Param("company") Company company,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}