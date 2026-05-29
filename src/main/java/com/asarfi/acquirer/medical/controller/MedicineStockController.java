package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.MedicineStockDto;
import com.asarfi.acquirer.medical.service.MedicineStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicine-stocks")
@RequiredArgsConstructor
public class MedicineStockController {

    private final MedicineStockService medicineStockService;

    @PostMapping
    public MedicineStockDto addStock(
            @RequestBody MedicineStockDto medicineStockDto
    ) {
        return medicineStockService.addStock(medicineStockDto);
    }

    @GetMapping("/company/{companyId}")
    public List<MedicineStockDto> getStockByCompany(
            @PathVariable Long companyId
    ) {
        return medicineStockService.getStockByCompany(companyId);
    }

    @GetMapping("/company/{companyId}/low-stock")
    public List<MedicineStockDto> getLowStockMedicines(
            @PathVariable Long companyId,
            @RequestParam Integer quantity
    ) {
        return medicineStockService.getLowStockMedicines(
                companyId,
                quantity
        );
    }


    @GetMapping("/company/{companyId}/expiring-soon")
    public List<MedicineStockDto> getExpiringSoonMedicines(
            @PathVariable Long companyId,
            @RequestParam Integer days
    ) {
        return medicineStockService.getExpiringSoonMedicines(
                companyId,
                days
        );
    }
}