package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.MedicineDto;
import com.asarfi.acquirer.medical.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    public MedicineDto createMedicine(
            @RequestBody MedicineDto medicineDto
    ) {
        return medicineService.createMedicine(medicineDto);
    }

    @GetMapping("/search")
    public List<MedicineDto> searchMedicines(
            @RequestParam Long companyId,
            @RequestParam String keyword
    ) {
        return medicineService.searchMedicines(companyId, keyword);
    }

    @PutMapping("/{medicineId}")
    public MedicineDto updateMedicine(
            @PathVariable Long medicineId,
            @RequestBody MedicineDto medicineDto
    ) {
        return medicineService.updateMedicine(medicineId, medicineDto);
    }

    @DeleteMapping("/{medicineId}")
    public String deleteMedicine(
            @PathVariable Long medicineId
    ) {
        return medicineService.deleteMedicine(medicineId);
    }
}