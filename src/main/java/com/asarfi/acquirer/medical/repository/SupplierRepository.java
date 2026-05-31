package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {

    List<Supplier> findByCompanyAndActiveTrueOrderByNameAsc(
            Company company
    );
}