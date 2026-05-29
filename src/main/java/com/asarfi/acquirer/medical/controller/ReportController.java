package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.SalesReportDto;
import com.asarfi.acquirer.medical.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}