package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.MedicineReturn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineReturnRepository extends JpaRepository<MedicineReturn, Long> {

    List<MedicineReturn> findByCompanyOrderByCreatedAtDesc(Company company);
}