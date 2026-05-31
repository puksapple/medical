package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.SupplierDto;
import com.asarfi.acquirer.medical.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public SupplierDto createSupplier(
            @RequestBody SupplierDto supplierDto
    ) {
        return supplierService.createSupplier(supplierDto);
    }

    @GetMapping("/company/{companyId}")
    public List<SupplierDto> getSuppliers(
            @PathVariable Long companyId
    ) {
        return supplierService.getSuppliers(companyId);
    }

    @PutMapping("/{supplierId}")
    public SupplierDto updateSupplier(
            @PathVariable Long supplierId,
            @RequestBody SupplierDto supplierDto
    ) {
        return supplierService.updateSupplier(
                supplierId,
                supplierDto
        );
    }

    @DeleteMapping("/{supplierId}")
    public String deleteSupplier(
            @PathVariable Long supplierId
    ) {
        return supplierService.deleteSupplier(supplierId);
    }
}