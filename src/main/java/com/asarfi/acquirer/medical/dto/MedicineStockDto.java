package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MedicineStockDto {

    private Long id;

    private Long companyId;

    private Long medicineId;

    private String medicineName;

    private Integer quantity;

    private String batchNo;

    private LocalDate expiryDate;

    private Long purchaseId;

    private String supplierName;
}