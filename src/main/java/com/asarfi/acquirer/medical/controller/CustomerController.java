package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.CustomerDto;
import com.asarfi.acquirer.medical.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerDto createCustomer(
            @RequestBody CustomerDto customerDto
    ) {
        return customerService.createCustomer(customerDto);
    }

    @GetMapping("/company/{companyId}")
    public List<CustomerDto> getCustomers(
            @PathVariable Long companyId
    ) {
        return customerService.getCustomers(companyId);
    }

    @PutMapping("/{customerId}")
    public CustomerDto updateCustomer(
            @PathVariable Long customerId,
            @RequestBody CustomerDto customerDto
    ) {
        return customerService.updateCustomer(customerId, customerDto);
    }

    @DeleteMapping("/{customerId}")
    public String deleteCustomer(
            @PathVariable Long customerId
    ) {
        return customerService.deleteCustomer(customerId);
    }
}