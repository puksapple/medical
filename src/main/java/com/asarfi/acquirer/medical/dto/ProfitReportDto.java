package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProfitReportDto {

    private BigDecimal grossSales;

    private BigDecimal salesReturnAmount;

    private BigDecimal netSales;

    private BigDecimal totalCost;

    private BigDecimal grossProfit;

    private BigDecimal profitMarginPercentage;

    private List<ProfitMedicineDto> medicines;
}