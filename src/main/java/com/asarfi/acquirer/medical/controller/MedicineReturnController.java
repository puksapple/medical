package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.MedicineReturnDto;
import com.asarfi.acquirer.medical.service.MedicineReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicine-returns")
@RequiredArgsConstructor
public class MedicineReturnController {

    private final MedicineReturnService medicineReturnService;

    @PostMapping
    public MedicineReturnDto createReturn(
            @RequestBody MedicineReturnDto medicineReturnDto
    ) {
        return medicineReturnService.createReturn(medicineReturnDto);
    }


    @GetMapping("/company/{companyId}")
    public List<MedicineReturnDto> getReturnsByCompany(
            @PathVariable Long companyId
    ) {
        return medicineReturnService.getReturnsByCompany(companyId);
    }


    @GetMapping("/{returnId}")
    public MedicineReturnDto getReturnById(
            @PathVariable Long returnId
    ) {
        return medicineReturnService.getReturnById(returnId);
    }
}