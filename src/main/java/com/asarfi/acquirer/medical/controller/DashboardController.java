package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.dto.DashboardDto;
import com.asarfi.acquirer.medical.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/company/{companyId}")
    public DashboardDto getDashboard(
            @PathVariable Long companyId
    ) {
        return dashboardService.getDashboard(companyId);
    }

    @GetMapping("/company/{companyId}/recent-bills")
    public List<BillDto> getRecentBills(
            @PathVariable Long companyId
    ) {
        return dashboardService.getRecentBills(companyId);
    }
}