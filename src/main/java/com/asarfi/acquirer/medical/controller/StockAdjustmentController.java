package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.StockAdjustmentDto;
import com.asarfi.acquirer.medical.service.StockAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-adjustments")
@RequiredArgsConstructor
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

    @PostMapping
    public StockAdjustmentDto createAdjustment(
            @RequestBody StockAdjustmentDto stockAdjustmentDto
    ) {
        return stockAdjustmentService.createAdjustment(stockAdjustmentDto);
    }

    @GetMapping("/company/{companyId}")
    public List<StockAdjustmentDto> getAdjustmentsByCompany(
            @PathVariable Long companyId
    ) {
        return stockAdjustmentService.getAdjustmentsByCompany(companyId);
    }
}