package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.MedicineStockDto;
import com.asarfi.acquirer.medical.dto.PurchaseReportDto;
import com.asarfi.acquirer.medical.dto.SalesReportDto;
import com.asarfi.acquirer.medical.dto.StockAdjustmentDto;
import com.asarfi.acquirer.medical.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    public SalesReportDto getSalesReport(
            @RequestParam Long companyId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        return reportService.getSalesReport(
                companyId,
                fromDate,
                toDate
        );
    }


    @GetMapping("/purchases")
    public PurchaseReportDto getPurchaseReport(
            @RequestParam Long companyId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        return reportService.getPurchaseReport(
                companyId,
                fromDate,
                toDate
        );
    }

    @GetMapping("/stock")
    public List<MedicineStockDto> getStockReport(
            @RequestParam Long companyId
    ) {
        return reportService.getStockReport(companyId);
    }


    @GetMapping("/stock-adjustments")
    public List<StockAdjustmentDto> getStockAdjustmentReport(
            @RequestParam Long companyId
    ) {
        return reportService.getStockAdjustmentReport(companyId);
    }
}