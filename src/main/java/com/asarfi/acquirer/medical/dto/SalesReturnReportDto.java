package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SalesReturnReportDto {

    private BigDecimal totalReturnAmount;

    private Integer totalReturns;

    private List<MedicineReturnDto> returns;
}