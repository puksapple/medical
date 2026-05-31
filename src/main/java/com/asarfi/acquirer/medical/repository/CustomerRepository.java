package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByCompanyAndActiveTrueOrderByNameAsc(Company company);
}