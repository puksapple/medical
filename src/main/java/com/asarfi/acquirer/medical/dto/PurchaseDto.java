package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PurchaseDto {

    private Long id;

    private Long companyId;

    private String supplierName;

    private String invoiceNumber;

    private BigDecimal totalAmount;

    private List<PurchaseItemDto> items;

    private Long supplierId;


}