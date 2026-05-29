package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Medicine;
import com.asarfi.acquirer.medical.entity.MedicineStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicineStockRepository extends JpaRepository<MedicineStock, Long> {

    List<MedicineStock> findByCompany(Company company);

    List<MedicineStock> findByCompanyAndMedicine(
            Company company,
            Medicine medicine
    );

    List<MedicineStock> findByCompanyAndMedicineOrderByExpiryDateAsc(
            Company company,
            Medicine medicine
    );

    List<MedicineStock> findByCompanyAndQuantityLessThanEqual(
            Company company,
            Integer quantity
    );

    List<MedicineStock> findByCompanyAndExpiryDateBetween(
            Company company,
            LocalDate startDate,
            LocalDate endDate
    );

    List<MedicineStock> findByCompanyAndMedicineAndBatchNo(
            Company company,
            Medicine medicine,
            String batchNo
    );
}