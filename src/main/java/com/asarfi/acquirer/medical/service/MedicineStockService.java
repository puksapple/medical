package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.MedicineStockDto;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Medicine;
import com.asarfi.acquirer.medical.entity.MedicineStock;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.MedicineRepository;
import com.asarfi.acquirer.medical.repository.MedicineStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineStockService {

    private final MedicineStockRepository medicineStockRepository;
    private final CompanyRepository companyRepository;
    private final MedicineRepository medicineRepository;

    public MedicineStockDto addStock(MedicineStockDto dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        MedicineStock stock = new MedicineStock();
        stock.setCompany(company);
        stock.setMedicine(medicine);
        stock.setQuantity(dto.getQuantity());
        stock.setBatchNo(dto.getBatchNo());
        stock.setExpiryDate(dto.getExpiryDate());
        stock.setCreatedAt(LocalDateTime.now());

        MedicineStock savedStock = medicineStockRepository.save(stock);

        MedicineStockDto response = new MedicineStockDto();
        response.setId(savedStock.getId());
        response.setCompanyId(company.getId());
        response.setMedicineId(medicine.getId());
        response.setMedicineName(medicine.getName());
        response.setQuantity(savedStock.getQuantity());
        response.setBatchNo(savedStock.getBatchNo());
        response.setExpiryDate(savedStock.getExpiryDate());

        return response;
    }

    public List<MedicineStockDto> getStockByCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<MedicineStock> stocks = medicineStockRepository.findByCompany(company);

        return stocks.stream().map(stock -> {
            MedicineStockDto dto = new MedicineStockDto();

            dto.setId(stock.getId());
            dto.setCompanyId(company.getId());
            dto.setMedicineId(stock.getMedicine().getId());
            dto.setMedicineName(stock.getMedicine().getName());
            dto.setQuantity(stock.getQuantity());
            dto.setBatchNo(stock.getBatchNo());
            dto.setExpiryDate(stock.getExpiryDate());

            return dto;
        }).toList();
    }

    public List<MedicineStockDto> getLowStockMedicines(
            Long companyId,
            Integer quantity
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<MedicineStock> stocks =
                medicineStockRepository.findByCompanyAndQuantityLessThanEqual(
                        company,
                        quantity
                );

        return stocks.stream().map(stock -> {
            MedicineStockDto dto = new MedicineStockDto();

            dto.setId(stock.getId());
            dto.setCompanyId(company.getId());
            dto.setMedicineId(stock.getMedicine().getId());
            dto.setMedicineName(stock.getMedicine().getName());
            dto.setQuantity(stock.getQuantity());
            dto.setBatchNo(stock.getBatchNo());
            dto.setExpiryDate(stock.getExpiryDate());

            return dto;
        }).toList();
    }

    public List<MedicineStockDto> getExpiringSoonMedicines(
            Long companyId,
            Integer days
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);

        List<MedicineStock> stocks =
                medicineStockRepository.findByCompanyAndExpiryDateBetween(
                        company,
                        today,
                        futureDate
                );

        return stocks.stream().map(stock -> {
            MedicineStockDto dto = new MedicineStockDto();

            dto.setId(stock.getId());
            dto.setCompanyId(company.getId());
            dto.setMedicineId(stock.getMedicine().getId());
            dto.setMedicineName(stock.getMedicine().getName());
            dto.setQuantity(stock.getQuantity());
            dto.setBatchNo(stock.getBatchNo());
            dto.setExpiryDate(stock.getExpiryDate());

            return dto;
        }).toList();
    }
}