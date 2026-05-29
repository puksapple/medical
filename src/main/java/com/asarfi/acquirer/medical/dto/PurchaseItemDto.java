package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PurchaseItemDto {

    private Long medicineId;

    private String medicineName;

    private Integer quantity;

    private BigDecimal purchasePrice;

    private BigDecimal subtotal;

    private String batchNo;

    private LocalDate expiryDate;
}