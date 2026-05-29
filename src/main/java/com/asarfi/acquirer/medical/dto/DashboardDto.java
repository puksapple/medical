package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardDto {

    private Long totalMedicines;

    private Long totalBills;

    private BigDecimal totalSales;

    private BigDecimal todaySales;
}