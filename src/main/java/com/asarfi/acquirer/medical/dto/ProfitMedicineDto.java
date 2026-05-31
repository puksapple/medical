package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfitMedicineDto {

    private Long medicineId;

    private String medicineName;

    private Integer quantitySold;

    private BigDecimal salesAmount;

    private BigDecimal costAmount;

    private BigDecimal profitAmount;
}