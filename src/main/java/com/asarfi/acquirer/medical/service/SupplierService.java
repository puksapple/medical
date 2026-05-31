package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.SupplierDto;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Supplier;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final CompanyRepository companyRepository;

    public SupplierDto createSupplier(SupplierDto dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Supplier supplier = new Supplier();

        supplier.setCompany(company);
        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setActive(true);

        return mapToDto(
                supplierRepository.save(supplier)
        );
    }

    public List<SupplierDto> getSuppliers(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return supplierRepository
                .findByCompanyAndActiveTrueOrderByNameAsc(company)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private SupplierDto mapToDto(Supplier supplier) {

        SupplierDto dto = new SupplierDto();

        dto.setId(supplier.getId());
        dto.setCompanyId(supplier.getCompany().getId());
        dto.setName(supplier.getName());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        dto.setActive(supplier.getActive());

        return dto;
    }


    public SupplierDto updateSupplier(
            Long supplierId,
            SupplierDto dto
    ) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());

        return mapToDto(
                supplierRepository.save(supplier)
        );
    }


    public String deleteSupplier(Long supplierId) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setActive(false);

        supplierRepository.save(supplier);

        return "Supplier deactivated successfully";
    }
}