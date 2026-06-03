package com.asarfi.acquirer.medical.dto;

import com.asarfi.acquirer.medical.entity.enums.MedicineUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BillItemDto {

    private Long id;

    private Long medicineId;

    private String medicineName;

    private Integer quantity;

    private MedicineUnit saleUnit;

    private Integer stockQuantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}