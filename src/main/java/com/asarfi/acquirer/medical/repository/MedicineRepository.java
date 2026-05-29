package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByCompanyAndNameContainingIgnoreCase(
            Company company,
            String name
    );

    Long countByCompany(Company company);

    List<Medicine> findByCompanyAndActiveTrueAndNameContainingIgnoreCase(
            Company company,
            String name
    );

    Long countByCompanyAndActiveTrue(Company company);
}