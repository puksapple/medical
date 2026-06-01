package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MedicineReturnItemDto {

    private Long billItemId;

    private Long medicineId;

    private String medicineName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}