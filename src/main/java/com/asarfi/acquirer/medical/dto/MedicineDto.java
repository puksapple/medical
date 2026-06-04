package com.asarfi.acquirer.medical.dto;

import com.asarfi.acquirer.medical.entity.enums.MedicineUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MedicineDto {

    private Long id;

    private Long companyId;

    private String name;

    private String genericName;

    private BigDecimal individualUnitPrice;
    private BigDecimal packUnitPrice;

    private Boolean active;

    private MedicineUnit baseUnit;

    private MedicineUnit packUnit;

    private Integer unitsPerPack;

    private Integer stockQuantity;
}