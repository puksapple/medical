package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PurchaseReportDto {

    private BigDecimal totalPurchaseAmount;

    private Integer totalPurchases;

    private List<PurchaseDto> purchases;
}