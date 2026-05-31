package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.CustomerDto;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Customer;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    public CustomerDto createCustomer(CustomerDto dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Customer customer = new Customer();
        customer.setCompany(company);
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setDiscountPercentage(
                dto.getDiscountPercentage() != null
                        ? dto.getDiscountPercentage()
                        : BigDecimal.ZERO
        );
        customer.setActive(true);

        return mapToDto(customerRepository.save(customer));
    }

    public List<CustomerDto> getCustomers(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return customerRepository.findByCompanyAndActiveTrueOrderByNameAsc(company)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public CustomerDto updateCustomer(Long customerId, CustomerDto dto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        if (dto.getDiscountPercentage() != null) {
            customer.setDiscountPercentage(dto.getDiscountPercentage());
        }

        return mapToDto(customerRepository.save(customer));
    }

    public String deleteCustomer(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setActive(false);
        customerRepository.save(customer);

        return "Customer deactivated successfully";
    }

    private CustomerDto mapToDto(Customer customer) {

        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setCompanyId(customer.getCompany().getId());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setDiscountPercentage(customer.getDiscountPercentage());
        dto.setActive(customer.getActive());

        return dto;
    }
}