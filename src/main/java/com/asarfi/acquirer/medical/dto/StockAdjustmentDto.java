package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAdjustmentDto {

    private Long id;
    private Long companyId;
    private Long medicineId;
    private String medicineName;
    private Integer quantity;
    private String adjustmentType;
    private String reason;
}