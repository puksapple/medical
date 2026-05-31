package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.StockAdjustmentDto;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final CompanyRepository companyRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineStockRepository medicineStockRepository;


    @Transactional
    public StockAdjustmentDto createAdjustment(StockAdjustmentDto dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        List<MedicineStock> stocks =
                medicineStockRepository.findByCompanyAndMedicineOrderByExpiryDateAsc(
                        company,
                        medicine
                );

        if ("DECREASE".equalsIgnoreCase(dto.getAdjustmentType())) {

            int availableQuantity = stocks.stream()
                    .mapToInt(MedicineStock::getQuantity)
                    .sum();

            if (availableQuantity < dto.getQuantity()) {
                throw new RuntimeException("Not enough stock to decrease");
            }

            int remainingQuantity = dto.getQuantity();

            for (MedicineStock stock : stocks) {

                if (remainingQuantity <= 0) {
                    break;
                }

                if (stock.getQuantity() >= remainingQuantity) {
                    stock.setQuantity(stock.getQuantity() - remainingQuantity);
                    medicineStockRepository.save(stock);
                    remainingQuantity = 0;
                } else {
                    remainingQuantity = remainingQuantity - stock.getQuantity();
                    stock.setQuantity(0);
                    medicineStockRepository.save(stock);
                }
            }

        } else if ("INCREASE".equalsIgnoreCase(dto.getAdjustmentType())) {

            if (stocks.isEmpty()) {
                MedicineStock stock = new MedicineStock();
                stock.setCompany(company);
                stock.setMedicine(medicine);
                stock.setQuantity(dto.getQuantity());
                stock.setCreatedAt(LocalDateTime.now());

                medicineStockRepository.save(stock);
            } else {
                MedicineStock stock = stocks.get(0);
                stock.setQuantity(stock.getQuantity() + dto.getQuantity());

                medicineStockRepository.save(stock);
            }

        } else {
            throw new RuntimeException("Invalid adjustment type");
        }

        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setCompany(company);
        adjustment.setMedicine(medicine);
        adjustment.setQuantity(dto.getQuantity());
        adjustment.setAdjustmentType(dto.getAdjustmentType());
        adjustment.setReason(dto.getReason());
        adjustment.setCreatedAt(LocalDateTime.now());

        StockAdjustment savedAdjustment =
                stockAdjustmentRepository.save(adjustment);

        StockAdjustmentDto response = new StockAdjustmentDto();
        response.setId(savedAdjustment.getId());
        response.setCompanyId(company.getId());
        response.setMedicineId(medicine.getId());
        response.setMedicineName(medicine.getName());
        response.setQuantity(savedAdjustment.getQuantity());
        response.setAdjustmentType(savedAdjustment.getAdjustmentType());
        response.setReason(savedAdjustment.getReason());

        return response;
    }


    @Transactional
    public List<StockAdjustmentDto> getAdjustmentsByCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<StockAdjustment> adjustments =
                stockAdjustmentRepository.findByCompanyOrderByCreatedAtDesc(company);

        return adjustments.stream().map(adjustment -> {

            StockAdjustmentDto dto = new StockAdjustmentDto();
            dto.setId(adjustment.getId());
            dto.setCompanyId(company.getId());
            dto.setMedicineId(adjustment.getMedicine().getId());
            dto.setMedicineName(adjustment.getMedicine().getName());
            dto.setQuantity(adjustment.getQuantity());
            dto.setAdjustmentType(adjustment.getAdjustmentType());
            dto.setReason(adjustment.getReason());

            return dto;

        }).toList();
    }
}